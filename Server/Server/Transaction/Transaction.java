package Server.Transaction;

import Server.Interface.IResourceManager;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Transaction {
    private static final long TIMEOUT = 60 * 1000; // timeout in milliseconds - The timeout is long for manual testing

    private final int transactionId;
    private final Map<String, IResourceManager> resourceManagersInvolved = new ConcurrentHashMap<>();
    private final AtomicBoolean isAborted = new AtomicBoolean(false);

    private final AtomicLong lastOperationTimestamp = new AtomicLong();

    final Object lock = new Object();

    Transaction(int transactionId) {
        this.transactionId = transactionId;
        lastOperationTimestamp.set(System.currentTimeMillis());
    }

    /**
     * Adds a resource manager to the list of those that are involved in the transaction.
     * @param resourceManager The resource manager.
     * @param resourceName The name of the resource (passed as parameter so as not to have a remote invocation just to get the name).
     */
    public void addResourceManager(IResourceManager resourceManager, String resourceName) {
        lastOperationTimestamp.set(System.currentTimeMillis());

        if (!isAborted.get()) {
            resourceManagersInvolved.put(resourceName, resourceManager);
        }
    }

    /**
     * Checks if the transaction has timed out.
     * @return Return true for timed out, false otherwise.
     */
    boolean checkForTimeout() {
        return lastOperationTimestamp.get() + TIMEOUT < System.currentTimeMillis();
    }

    public synchronized boolean commit() {
        if (isAborted.get()) return false;

        boolean committed = true;

        for (IResourceManager resourceManager : resourceManagersInvolved.values()) {
            try {
                resourceManager.commit(transactionId);
            } catch(RemoteException e) {
                committed = false;
            }
        }

        return committed;
    }

    public synchronized void abort() {
        if (isAborted.get()) return;

        resourceManagersInvolved.values().forEach(resourceManager -> {
            try
            {
                resourceManager.abort(transactionId);
            }
            catch (RemoteException ignored) { }
        });

        isAborted.set(true);
    }

    public int getTransactionId() {
        return transactionId;
    }

    public boolean isAborted() {
        return isAborted.get();
    }
}
