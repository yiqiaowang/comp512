package Server.RMI.middleware;

import Server.Transaction.InvalidTransactionException;
import Server.Transaction.TransactionAbortedException;

import java.rmi.RemoteException;

public interface SupplierWithRemoteException<T> {
    T operation() throws RemoteException, TransactionAbortedException, InvalidTransactionException;
}
