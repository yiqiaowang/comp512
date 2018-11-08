package Server.Transaction;

import Server.LockManager.DeadlockException;
import Server.LockManager.LockManager;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionManager {
    private final Map<Integer, Transaction> transactions = new ConcurrentHashMap<>();
    private final AtomicInteger transactionIdGenerator = new AtomicInteger(0);
    private final LockManager lockManager = new LockManager();


    public int startTransaction() {
        int transactionId = transactionIdGenerator.incrementAndGet();
        Transaction transaction = new Transaction(transactionId);
        transactions.put(transactionId, transaction);

        return transactionId;
    }

    public boolean requestLocksOnResources(int transactionId,ResourceLockRequest... resourceLockRequests) {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return false;
        }

        synchronized (transaction.lock)
        {
            for (ResourceLockRequest resourceLockRequest : resourceLockRequests) {
                try {
                    boolean lockAcquired = lockManager.Lock(transactionId, resourceLockRequest.getResourceName(), resourceLockRequest.getLockType());
                    if (!lockAcquired) {
                        transaction.abort();
                        return false;
                    }
                } catch (DeadlockException e) {
                    transaction.abort();
                    return false;
                }

                transaction.addResourceManager(resourceLockRequest.getResourceManager(), resourceLockRequest.getResourceName());
            }

            return true;
        }
    }

    public boolean commit(int transactionId) throws RemoteException {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return false;
        }

        boolean commitResult = transaction.commit();
        transactions.remove(transactionId);
        return commitResult;
    }

    public void abort(int transactionId) {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return;
        }

        transaction.abort();
        transactions.remove(transactionId);
    }

    public boolean isOngoingTransaction(int transactionId) {
        return transactions.containsKey(transactionId);
    }
}
