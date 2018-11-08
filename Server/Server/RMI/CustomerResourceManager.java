package Server.RMI;

import Server.Common.ReservedItem;
import Server.RMI.middleware.ICustomerResourceManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CustomerResourceManager extends RMIResourceManager implements ICustomerResourceManager {
    private final String ID;

    public CustomerResourceManager(String name, String id) {
        super(name);
        ID = id;
    }

    @Override
    public void reserveCustomer(int xid, int customerID, String itemKey, String location, int price) {
        String key = itemKey + "-" + customerID;

        ReservedItem reservedItem = (ReservedItem)readData(xid, key);

        if (reservedItem == null) {
            reservedItem = new ReservedItem(key, location, 1, price);
            writeData(xid, key, reservedItem);
        } else {
            reservedItem.setCount(reservedItem.getCount() + 1);
            reservedItem.setPrice(price);
        }
    }




    public static void main(String args[])
    {
        if (args.length > 0)
        {
            s_serverName = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }

        // Create the RMI server entry
        try {
            // Create a new Server object
            CustomerResourceManager server = new CustomerResourceManager(s_serverName, "CUSTOMERS");

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
