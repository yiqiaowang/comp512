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


public class MiddlewareWorker implements Runnable {
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private TCPMiddleware middleware; // Shared amongst threads???

    public MiddlewareWorker(Socket socket, TCPMiddleware middleware) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.middleware = middleware;
    }

    public void run() {
        System.out.println("Launched Thread");
        // Read the initial request and determine if this is a consumer or a resource manager 

        try {
            ProcedureRequest request = (ProcedureRequest) this.in.readObject();

            // Handle resource manager registration
            if (request.getProcedure() == Procedure.RegisterResourceManager) {
                // this.middleware.registerResourceManager(request); 
                System.out.println("Launched thread to register resource manager ");
            }

            // Handle consumer
            if (request.getProcedure() != Procedure.RemoveResourceManager) {
                // ProcedureResponse response = this.middleware.executeRequest(request);  
                // this.out.writeObject(response);
                System.out.println("Launched thread to handle consumer");
            }
            
        } catch(Exception e){
            System.out.println("Something bad happened in a middleware worker thread!!!");
            e.printStackTrace();
        }
    }
}
