// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.TCP;

import Server.Common.ResourceManager;
import Server.Common.Procedure;
import Server.Common.ProcedureRequest;
import Server.Common.ProcedureResponse;
import Server.Common.Car;
import Server.Common.Flight;
import Server.Common.Room;

import java.net.*;
import java.io.*;
import java.lang.*;


public class FlightResourceManager extends TCPResourceManager 
{
    private static String managerName = "FlightResourceManager";
    private static String middlewareServer = "localhost";
    private static int middlewarePort = 6666;
    private static int managerID = 1;

    public static void main(String args[])
    {
        try {
            if (args.length > 0) {
                middlewareServer = args[0];
            }
            if (args.length > 1) {
                middlewarePort = Integer.parseInt(args[1]);
            }
            if (args.length > 2) {
                System.err.println((char)27 + "[31;1mResourceManager exception: " + (char)27 + "[0mUsage: java <ResourceManager> [server_hostname [server_port]]");
                System.exit(1);
            }
            // Create a new Server object
            FlightResourceManager manager = new FlightResourceManager(FlightResourceManager.managerName);
            
            // Register with middleware
            manager.start(FlightResourceManager.middlewareServer, FlightResourceManager.middlewarePort);
            manager.setupStreams();
            manager.registerMiddleware();

            // Handle Requests
            while (true) {
                ProcedureRequest request = manager.receiveRequest();
                // Run worker thread here...
                ResourceManagerWorker w = new ResourceManagerWorker(request, manager);
                Thread t = new Thread(w);
                t.start();

                // Reply is sent out by the worker thread
            }
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

    public void registerMiddleware() throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.RegisterResourceManager);
        // request.setLocation(server);
        // requset.setResourceID(port);
        request.setReserveID(FlightResourceManager.managerID);
        this.out.writeObject(request);
        ProcedureResponse response = (ProcedureResponse) this.in.readObject();
        System.out.println(response.getProcedure());
        // can check for success response here
    }

    public FlightResourceManager(String name) {
        super(name);
    }
}
