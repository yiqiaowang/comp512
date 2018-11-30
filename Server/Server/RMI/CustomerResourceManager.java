package Server.RMI;

import Server.Common.Customer;
import Server.RMI.middleware.ICustomerResourceManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CustomerResourceManager extends RMIResourceManager implements ICustomerResourceManager {
    public CustomerResourceManager(String name) {
        super(name);
    }


    @Override
    public void reserveCustomer(int xid, int customerID, String itemKey, String location, int price) {
        String key = Customer.getKey(customerID);

        Customer customer = (Customer)readData(xid, key);

        if (customer != null) {
            customer.reserve(itemKey, location, price);
        }

        writeData(xid, key, customer);
    }




    public static void main(String args[])
    {
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        s_serverName = "CUSTOMERS";

        // Create the RMI server entry
        try {
            // Create a new Server object
            CustomerResourceManager server = new CustomerResourceManager(s_serverName);

            // Dynamically generate the stub (client proxy)
            ICustomerResourceManager resourceManager = (ICustomerResourceManager) UnicastRemoteObject.exportObject(server, 0);

            // Bind the remote object's stub in the registry
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(port);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(port);
            }
            final Registry registry = l_registry;
            registry.rebind(s_rmiPrefix + s_serverName, resourceManager);

            shutdownHook = new Thread(() -> {
                try {
                    registry.unbind(s_rmiPrefix + s_serverName);
                    System.out.println("'" + s_serverName + "' resource manager unbound");
                }
                catch(Exception e) {
                    System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
                    e.printStackTrace();
                }
            });

            Runtime.getRuntime().addShutdownHook(shutdownHook);
            System.out.println("'" + s_serverName + "' resource manager server ready and bound to '" + s_rmiPrefix + s_serverName + "'");
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
    }
}
