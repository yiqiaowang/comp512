package Client;

// import Server.Interface.*;
// import Server.Common.Procedure;
// import Server.Common.ProcedureRequest;
//
import Server.Common.ClientCommunicationManager;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient extends Client
{
    private static String server = "localhost";
    private static int port = 6666;

    // TCP client related members
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static void main(String args[])
    {	
        // Set the security policy
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }

        // Setup communication Manager
        try {
            TCPClient client = new TCPClient();
            client.connectServer();
            client.start();
        } catch (Exception e) {    
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }

    }

    public TCPClient() {
        super();
    }
    
    public void connectServer() throws IOException, UnknownHostException {
        this.communicationManager = new ClientCommunicationManager(this.server, this.port);
    }

    public void connectServer(String server, int port) throws IOException, UnknownHostException {
        this.communicationManager = new ClientCommunicationManager(server, port);
    }

    public void stopConnection() throws IOException {
        this.communicationManager.stopConnection();
    }
}
