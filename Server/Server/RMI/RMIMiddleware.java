package Server.RMI;

import Server.Common.RmiResourceManagerFactory;
import Server.Common.Services;
import Server.Interface.IResourceManager;
import Server.RMI.middleware.ICustomerResourceManager;
import Server.RMI.middleware.Middleware;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class RMIMiddleware {
    private static final String s_rmiPrefix = "groupFive_";
    private static final String s_serverName = "Middleware";
    private static final String name = s_rmiPrefix + s_serverName;

    private static Thread shutdownHook;

    private static int port;


    public static IResourceManager initializeMiddleware(int port, String[] args) {
        IResourceManager flightsResourceManager = hostAndPortToResourceManager(args[0], Services.FLIGHTS.toString());
        IResourceManager carsResourceManager = hostAndPortToResourceManager(args[1], Services.CARS.toString());
        IResourceManager roomsResourceManager = hostAndPortToResourceManager(args[2], Services.ROOMS.toString());
        ICustomerResourceManager customerResourceManager = hostAndPortToCustomerResourceManager(args[3], Services.CUSTOMERS.toString());

        return initializeMiddleware(port, flightsResourceManager, carsResourceManager, roomsResourceManager, customerResourceManager);
    }


    public static IResourceManager initializeMiddleware(int port, IResourceManager flightsResourceManager, IResourceManager carsResourceManager, IResourceManager roomsResourceManager, ICustomerResourceManager customerResourceManager) {
        try {
            IResourceManager middleware = new Middleware(flightsResourceManager, carsResourceManager, roomsResourceManager, customerResourceManager);

            // Dynamically generate the stub (client proxy)
            IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(middleware, 0);

            // Bind the remote object's stub in the registry
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(port);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(port);
            }
            final Registry registry = l_registry;
            registry.rebind(name, resourceManager);

            shutdownHook = new Thread(() -> {
                try {
                    registry.unbind(name);
                    System.out.println("'" + s_serverName + "' resource manager unbound");
                }
                catch(Exception e) {
                    System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
                    e.printStackTrace();
                }
            });
            Runtime.getRuntime().addShutdownHook(shutdownHook);

            return resourceManager;
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public static boolean shutdown() throws RemoteException {
        try {
            if (shutdownHook != null) {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            }
        } catch (Exception ignored) {

        }

        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(port);
        }

        boolean result = true;

        try {
            registry.unbind(name);
            System.out.println("'" + s_serverName + "' resource manager unbound");
        }
        catch(Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            result = false;
        }

        new Thread(() -> {
            System.out.println("Preparing to shutdown Middleware");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }
            System.exit(0);
        }).start();

        return result;
    }

    public static void main(String[] args) throws RemoteException {
        if (args.length > 4) {
            port = Integer.parseInt(args[0]);
            initializeMiddleware(port, Arrays.copyOfRange(args, 1, args.length));
        } else {
            System.out.println("Missing parameters");
        }
    }

    private static IResourceManager hostAndPortToResourceManager(String hostAndPort, String name) {
        String[] separatedHostAndPort = hostAndPort.split(":");
        String host = separatedHostAndPort[0];
        int port = Integer.parseInt(separatedHostAndPort[1]);

        return RmiResourceManagerFactory.connectServer(host, port, name);
    }

    private static ICustomerResourceManager hostAndPortToCustomerResourceManager(String hostAndPort, String name) {
        String[] separatedHostAndPort = hostAndPort.split(":");
        String host = separatedHostAndPort[0];
        int port = Integer.parseInt(separatedHostAndPort[1]);

        return RmiResourceManagerFactory.connectCustomerServer(host, port, name);
    }
}
