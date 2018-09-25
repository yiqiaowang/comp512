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


public class RoomResourceManager extends TCPResourceManager 
{
    private static String managerName = "RoomResourceManager";
    private static String middlewareServer = "localhost";
    private static int middlewarePort = 6666;
    private static int managerID = 3;
    
    public static void main(String args[])
    {
        try {
            // Create a new Server object
            RoomResourceManager manager = new RoomResourceManager(RoomResourceManager.managerName);
            
            // Register with middleware
            manager.start(RoomResourceManager.middlewareServer, RoomResourceManager.middlewarePort);
            manager.setupStreams();
            manager.registerMiddleware();

            // Handle Requests
            while (true) {
                ProcedureRequest request = manager.receiveRequest();
                ProcedureResponse response = manager.executeRequest(request);
                manager.sendResponse(response);
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
        request.setReserveID(RoomResourceManager.managerID);
        out.writeObject(request);
        ProcedureResponse response = (ProcedureResponse) in.readObject();
        System.out.println(response.getProcedure());
        // can check for success response here
    }

    public RoomResourceManager(String name) {
        super(name);
    }
}
