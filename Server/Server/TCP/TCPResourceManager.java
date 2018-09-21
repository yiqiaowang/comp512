// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.TCP;

import Server.Common.ResourceManager;
import Server.Common.MethodLambda;
// import Server.Interface.IResourceManager;

import java.net.*;
import java.io.*;


public class TCPResourceManager extends ResourceManager 
{

    private static String s_serverName = "TCPServer";
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static void main(String args[])
    {
        // Create the TCP server entry
        try {
            // Create a new Server object
            TCPResourceManager server = new TCPResourceManager();
            server.start(6666);
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

    public void start(int port) throws IOException, ClassNotFoundException {
        System.out.println("Started TCP server");

        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        MethodLambda greeting = (MethodLambda) in.readObject();

        greeting.callMethod();
        // greeting.callMethod();

    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public TCPResourceManager()
    {
        super(s_serverName);
    }
}
