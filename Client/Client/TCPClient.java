package Client;

// import Server.Interface.*;
import Server.Common.Procedure;
import Server.Common.ProcedureRequest;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient
{
    // private static String s_serverHost = "localhost";
    // private static int s_serverPort = 6666;

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

        // Try to connect to server via TCP
        try {
            TCPClient client = new TCPClient();
            client.connectServer("127.0.0.1", 6666);

            ProcedureRequest addFlight = new ProcedureRequest(Procedure.AddFlight);

            client.executeProcedure(addFlight);
            client.stopConnection();
        } 
        catch (Exception e) {    
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public TCPClient()
    {
        super();
    }

    public void connectServer(String ip, int port) throws IOException, UnknownHostException
    {
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void executeProcedure(ProcedureRequest procedure) throws IOException
    {
        out.writeObject(procedure);
    }

    public void stopConnection() throws IOException
    {
        in.close();
        out.close();
        clientSocket.close();
    }
}
