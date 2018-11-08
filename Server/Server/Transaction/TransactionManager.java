package Server.Transaction;

import Server.LockManager.DeadlockException;
import Server.LockManager.LockManager;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionManager {
    private final Map<Integer, Transaction> transactions = new ConcurrentHashMap<>();
    private final AtomicInteger transactionIdGenerator = new AtomicInteger(0);
    private final LockManager lockManager = new LockManager();

    public TransactionManager() {
        Thread checkForTimeouts = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }

                List<Integer> timedOutTransactionIds = new ArrayList<>();

                for (Transaction transaction : transactions.values()) {
                    if (transaction.checkForTimeout()) {
                        timedOutTransactionIds.add(transaction.getTransactionId());
                    }
                }

                for (Integer timedOutTransactionId : timedOutTransactionIds) {
                    System.out.println("Transaction " + timedOutTransactionId + " has timed out");
                    transactions.remove(timedOutTransactionId);
                }
            }
        });
        checkForTimeouts.start();
    }


    public int startTransaction() {
        int transactionId = transactionIdGenerator.incrementAndGet();
        Transaction transaction = new Transaction(transactionId);
        transactions.put(transactionId, transaction);

        return transactionId;
    }

    public boolean requestLocksOnResources(int transactionId, ResourceLockRequest... resourceLockRequests) {
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
                        System.out.println("Failed to acquire " + resourceLockRequest.getLockType() + " on " + resourceLockRequest.getResourceName());
                        transaction.abort();
                        return false;
                    }
                } catch (DeadlockException e) {
                    System.out.println("Failed to acquire " + resourceLockRequest.getLockType() + " on " + resourceLockRequest.getResourceName() + " because of deadlock");
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
        lockManager.UnlockAll(transactionId);

        return commitResult;
    }

    public void abort(int transactionId) {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return;
        }

        transaction.abort();
        transactions.remove(transactionId);
        lockManager.UnlockAll(transactionId);
    }

    public boolean isOngoingTransaction(int transactionId) {
        return transactions.containsKey(transactionId);
    }

    public boolean startTransaction(int transactionId) {
        if (!isOngoingTransaction(transactionId)) {
            Transaction transaction = new Transaction(transactionId);
            transactions.put(transactionId, transaction);
            return true;
        } else {
            return false;
        }
    }
}
