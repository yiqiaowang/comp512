package Server.RMI.middleware;

import Server.Interface.IResourceManager;

import java.rmi.RemoteException;

public interface ICustomerResourceManager extends IResourceManager {
    void reserveCustomer(int customerID, String itemKey, String location, int price) throws RemoteException;
}
