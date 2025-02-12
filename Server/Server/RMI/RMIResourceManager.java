// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.RMI;

import Server.Common.ResourceManager;
import Server.Interface.IResourceManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIResourceManager extends ResourceManager 
{
	protected static String s_serverName = "Server";
	protected static String s_rmiPrefix = "groupFive_";
	protected static int port = 1099;

	static Thread shutdownHook;


	@Override
	public boolean shutdown() throws RemoteException {
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
			registry.unbind(s_rmiPrefix + s_serverName);
		} catch (NotBoundException e) {
			result = false;
		}

		new Thread(() -> {
			System.out.println("Preparing to shutdown " + s_serverName);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {

			}
			System.exit(0);
		}).start();

		return result;
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
			RMIResourceManager server = new RMIResourceManager(s_serverName);

			// Dynamically generate the stub (client proxy)
			IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(server, 0);

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
					String registryName = s_rmiPrefix + s_serverName;
					registry.unbind(registryName);
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



	public RMIResourceManager(String name)
	{
		super(name);
	}
}
