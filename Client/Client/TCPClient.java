package Client;

import Server.Common.ProcedureRequest;
import Server.Common.Procedure;
import Server.Common.ClientCommunicationManager;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient extends Client
{
    private static String middlewareServer = "localhost";
    private static int middlewarePort = 6666;

    public static void main(String args[])
    {	
        if (args.length > 0) {
            middlewareServer = args[0];
        }
        if (args.length > 1) {
            middlewarePort = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUsage: java client.TCPClient [server_hostname [server_port]]");
            System.exit(1);
        }

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
        connectServer(TCPClient.middlewareServer, TCPClient.middlewarePort);
    }

    public void connectServer(String server, int port) throws IOException, UnknownHostException {
        this.communicationManager = new ClientCommunicationManager(new Socket(server, port));
        this.communicationManager.setupStreams();
    }

    public void disconnectServer() throws IOException {
        this.communicationManager.stopConnection();
    }
}
