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


public class RoomResourceManager extends TCPResourceManager 
{
    private static String managerName = "RoomResourceManager";
    private static String middlewareServer = "localhost";
    private static int middlewarePort = 6666;
    private static int managerID = 3;
    
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
            RoomResourceManager manager = new RoomResourceManager(RoomResourceManager.managerName);
            
            // Register with middleware
            manager.start(RoomResourceManager.middlewareServer, RoomResourceManager.middlewarePort);
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
        request.setReserveID(RoomResourceManager.managerID);
        out.writeObject(request);
        ProcedureResponse response = (ProcedureResponse) in.readObject();
        System.out.println(response.getProcedure());
    }

    public RoomResourceManager(String name) {
        super(name);
    }
}
