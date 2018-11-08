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


	@Override
	public void abort(int xid) throws RemoteException {

	}

	@Override
	public boolean commit(int xid) throws RemoteException {
		return false;
	}


	private static void connectToMiddleware(String server, int port) {
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					middleware = (IResourceManager)registry.lookup(s_rmiPrefix + "Middleware");
					System.out.println("Connected to 'Middleware' server [" + server + ":" + port + "/" + s_rmiPrefix + "Middleware" + "]");
					break;
				}
				catch (NotBoundException | RemoteException e) {
					if (first) {
						System.out.println("Waiting for 'Middleware' server [" + server + ":" + port + "/" + s_rmiPrefix + "Middleware" + "]");
						first = false;
					}
				}
				Thread.sleep(500);
			}
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
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

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					String registryName = s_rmiPrefix + s_serverName;
					registry.unbind(registryName);
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
