package Server.Transaction;

import Server.Interface.IResourceManager;
import Server.LockManager.DeadlockException;
import Server.LockManager.LockManager;
import Server.RMI.middleware.SupplierWithRemoteException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static Server.LockManager.TransactionLockObject.LockType;

public class TransactionManager {
    private final Map<Integer, Transaction> transactions = new ConcurrentHashMap<>();
    private final AtomicInteger transactionIdGenerator = new AtomicInteger(0);
    private final LockManager lockManager = new LockManager();

    private final Map<Integer, SupplierWithRemoteException> operations = new ConcurrentHashMap<>();


    public int startTransaction() {
        int transactionId = transactionIdGenerator.incrementAndGet();
        Transaction transaction = new Transaction(transactionId);
        transactions.put(transactionId, transaction);

        return transactionId;
    }

    public boolean requestLockOnResource(int transactionId, IResourceManager resourceManager, String resourceName, LockType lockType) {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return false;
        }

        try {
            boolean lockAcquired = lockManager.Lock(transactionId, resourceName, lockType);
            if (!lockAcquired) {
                transaction.abort();
                return false;
            }
        } catch (DeadlockException e) {
            transaction.abort();
            return false;
        }

        transaction.addResourceManager(resourceManager, resourceName);

        return true;
    }

    public boolean addOperation(int transactionId, List<ResourceLockRequest> resources, SupplierWithRemoteException operation) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction == null) return false;

        for (ResourceLockRequest resource : resources) {
            boolean result = requestLockOnResource(transactionId, resource.getResourceManager(), resource.getResourceName(), resource.getLockType());
            if (!result) {
                abort(transactionId);
                return false;
            }
        }

        transaction.addOperation(operation);

        return true;
    }


    /**
     * Commits the transaction.
     * @param transactionId The transaction id
     * @return The list of results for the transaction if successful
     * @throws InvalidTransactionException if the transaction fails for any reason
     */
    public List commit(int transactionId) throws InvalidTransactionException {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            throw new InvalidTransactionException(transactionId);
        }

        boolean commitResult = transaction.commit();
        transactions.remove(transactionId);
        if (commitResult) {
            return transaction.getResults();
        } else {
            throw new InvalidTransactionException(transactionId);
        }
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
