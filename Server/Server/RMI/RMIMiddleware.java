package Server.RMI;

import Server.Common.RmiResourceManagerFactory;
import Server.Common.Services;
import Server.Interface.IResourceManager;
import Server.RMI.middleware.Middleware;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RMIMiddleware {
    private static final String s_rmiPrefix = "groupFive_";
    private static final String s_serverName = "Middleware";
    private static final String name = s_rmiPrefix + s_serverName;

    public static void main(String[] args) throws RemoteException {
        try {
            Map<Services, IResourceManager> resourceManagers = new HashMap<>();

            resourceManagers.put(Services.FLIGHTS, hostAndPortToResourceManager(args[0], Services.FLIGHTS.toString()));
            resourceManagers.put(Services.CARS, hostAndPortToResourceManager(args[1], Services.CARS.toString()));
            resourceManagers.put(Services.ROOMS, hostAndPortToResourceManager(args[2], Services.ROOMS.toString()));
            resourceManagers.put(Services.CUSTOMERS, hostAndPortToResourceManager(args[3], Services.CUSTOMERS.toString()));

            IResourceManager middleware = new Middleware(resourceManagers);



            // Dynamically generate the stub (client proxy)
            IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(middleware, 0);

            // Bind the remote object's stub in the registry
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(1099);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(1099);
            }
            final Registry registry = l_registry;
            registry.rebind(name, resourceManager);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    registry.unbind(name);
                    System.out.println("'" + s_serverName + "' resource manager unbound");
                }
                catch(Exception e) {
                    System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
                    e.printStackTrace();
                }
            }));
            System.out.println("'" + s_serverName + "' resource manager server ready and bound to '" + s_rmiPrefix + s_serverName + "'");
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static IResourceManager hostAndPortToResourceManager(String hostAndPort, String name) {
        String[] separatedHostAndPort = hostAndPort.split(":");
        String host = separatedHostAndPort[0];
        int port = Integer.parseInt(separatedHostAndPort[1]);

        return RmiResourceManagerFactory.connectServer(host, port, name);
    }
}
