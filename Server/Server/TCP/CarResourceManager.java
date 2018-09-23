// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.TCP;

import Server.Common.Procedure;
import Server.Common.ProcedureRequest;
import Server.Common.ProcedureResponse;
import Server.Common.Car;
import Server.Common.Flight;
import Server.Common.Room;

import java.net.*;
import java.io.*;


public class CarResourceManager extends TCPResourceManager 
{

    private static String s_serverName = "TCPServer";
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static void main(String args[])
    {
        try {
            // Create a new Server object
            TCPResourceManager server = new TCPResourceManager();
            server.start(6666);
            server.acceptConnection();
            server.setupStreams();

            // Handle Requests
            while (true) {
                ProcedureRequest request = server.receiveRequest();
                ProcedureResponse response = server.executeRequest(request);
                server.sendResponse(response);
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
}
