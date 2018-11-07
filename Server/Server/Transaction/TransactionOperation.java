package Server.Transaction;

import Server.RMI.middleware.SupplierWithRemoteException;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

public class TransactionOperation<T> implements SupplierWithRemoteException<T> {
    private final ITransactionOperation operation;
    private final List<ResourceLockRequest> requiredLocks;

    public TransactionOperation(List<ResourceLockRequest> requiredLocks, ITransactionOperation operation) {
        this.requiredLocks = Collections.unmodifiableList(requiredLocks);
        this.operation = operation;
    }


    public List<ResourceLockRequest> getResourceLockRequests() {
        return requiredLocks;
    }

    @Override
    public T operation() throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        return null;
    }
}
