package Server.Transaction;

import java.rmi.RemoteException;

public interface ITransactionOperation {
    void run() throws RemoteException;
}
