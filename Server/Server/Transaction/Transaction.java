package Server.Transaction;

import Server.Interface.IResourceManager;
import Server.Interface.InvalidTransactionException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Transaction implements Serializable {
    private static final long TIMEOUT = 60 * 1000; // timeout in milliseconds - The timeout is long for manual testing

    private int transactionId;
    private Map<String, IResourceManager> resourceManagersInvolved;
    private AtomicBoolean isAborted = new AtomicBoolean(false);

    private AtomicLong lastOperationTimestamp = new AtomicLong();

    Object lock;

    Transaction(int transactionId) {
        setup();
        this.transactionId = transactionId;
        lastOperationTimestamp.set(System.currentTimeMillis());
    }

    private void setup() {
        resourceManagersInvolved = new ConcurrentHashMap<>();
        isAborted = new AtomicBoolean(false);
        lastOperationTimestamp = new AtomicLong();
        lock = new Object();
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

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        System.out.println("Write object for transaction called");
        outputStream.writeInt(transactionId);
        System.out.println("isAborted: " + isAborted);
        outputStream.writeBoolean(isAborted.get());

        System.out.println("resource managers involved: " + resourceManagersInvolved);
        outputStream.writeInt(resourceManagersInvolved.size());

        for (Map.Entry<String, IResourceManager> resourceManagerEntry : resourceManagersInvolved.entrySet()) {
            outputStream.writeUTF(resourceManagerEntry.getKey());
            outputStream.writeObject(resourceManagerEntry.getValue());
        }

    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        System.out.println("Read object for transaction called");
        setup();

        transactionId = inputStream.readInt();
        isAborted.set(inputStream.readBoolean());

        lastOperationTimestamp.set(System.currentTimeMillis());

        int numResourceManagers = inputStream.readInt();
        for (int i = 0; i < numResourceManagers; i++) {
            String resourceManagerName = inputStream.readUTF();
            resourceManagersInvolved.put(resourceManagerName, (IResourceManager) inputStream.readObject());
        }
    }

    public boolean checkForCommit() throws InvalidTransactionException, RemoteException {
        if (isAborted.get()) return false;

        for (IResourceManager resourceManager : resourceManagersInvolved.values()) {
            if (!resourceManager.prepare(transactionId)) {
                abort();
                return false;
            }
        }

        return true;
    }
}
