package Server.Transaction;

import Server.LockManager.DeadlockException;
import Server.LockManager.LockManager;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private final int transactionId;
    private final LockManager lockManager;
    private final List<ITransactionOperation> transactionOperations = new ArrayList<>();

    private boolean isAborted = false;

    Transaction(int transactionId, LockManager lockManager) {
        this.transactionId = transactionId;
        this.lockManager = lockManager;
    }

    public boolean addOperation(TransactionOperation transactionOperation) {
        for (ResourceLockRequest resourceLockRequest: transactionOperation.getResourceLockRequests()) {
            try {
                boolean locked = lockManager.Lock(transactionId, resourceLockRequest.getResourceName(), resourceLockRequest.getLockType());
                if (!locked) {
                    abort();
                    return false;
                }
            } catch (DeadlockException e) {
                abort();
                return false;
            }
        }

        transactionOperations.add(transactionOperation);
        return true;
    }

    private void cleanup() {
        lockManager.UnlockAll(transactionId);
    }

    public boolean commit() throws RemoteException {
        for (ITransactionOperation transactionOperation : transactionOperations) {
            transactionOperation.run();
        }

        return true;
    }

    public void abort() {
        cleanup();
        isAborted = true;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public boolean isAborted() {
        return isAborted;
    }
}
