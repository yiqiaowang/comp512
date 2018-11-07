package Server.Transaction;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

public class TransactionOperation implements ITransactionOperation {
    private final ITransactionOperation operation;
    private final List<ResourceLockRequest> requiredLocks;

    public TransactionOperation(List<ResourceLockRequest> requiredLocks, ITransactionOperation operation) {
        this.requiredLocks = Collections.unmodifiableList(requiredLocks);
        this.operation = operation;
    }

    @Override
    public void run() throws RemoteException {
        operation.run();
    }

    public List<ResourceLockRequest> getResourceLockRequests() {
        return requiredLocks;
    }
}
