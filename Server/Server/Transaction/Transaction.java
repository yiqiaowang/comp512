package Server.Transaction;

import Server.Interface.IResourceManager;
import Server.RMI.middleware.SupplierWithRemoteException;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Transaction {
    private final int transactionId;
    private final Map<String, IResourceManager> resourceManagersInvolved = new ConcurrentHashMap<>();
    private final AtomicBoolean isAborted = new AtomicBoolean(false);
    private final List<SupplierWithRemoteException> operations = new Vector<>();
    private final List<Object> results = new Vector<>();

    Transaction(int transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Adds a resource manager to the list of those that are involved in the transaction.
     * @param resourceManager The resource manager.
     * @param resourceName The name of the resource (passed as parameter so as not to have a remote invocation just to get the name).
     */
    public void addResourceManager(IResourceManager resourceManager, String resourceName) {
        if (!isAborted.get()) {
            resourceManagersInvolved.put(resourceName, resourceManager);
        }
    }

    public synchronized void addOperation(SupplierWithRemoteException operation) {
        operations.add(operation);
    }

    public synchronized boolean commit() {
        if (isAborted.get()) return false;

        for (SupplierWithRemoteException operation : operations) {
            try {
                results.add(operation.operation());
            } catch (RemoteException | TransactionAbortedException | InvalidTransactionException e) {
                abort();
                return false;
            }
        }

        for (IResourceManager resourceManager : resourceManagersInvolved.values()) {
            try
            {
                resourceManager.commit(transactionId);
            }
            catch(RemoteException | InvalidTransactionException ignored) { }
        }

        return true;
    }

    public synchronized void abort() {
        if (isAborted.get()) return;

        resourceManagersInvolved.values().forEach(resourceManager -> {
            try
            {
                resourceManager.abort(transactionId);
            }
            catch (RemoteException ignored) {
                /*
                    If it fails to connect to the resource manager, it will abort after
                    it times out anyways.
                 */
            }
        });

        isAborted.set(true);
    }

    public int getTransactionId() {
        return transactionId;
    }

    public List getResults() {
        return Collections.unmodifiableList(results);
    }

    public boolean isAborted() {
        return isAborted.get();
    }
}
