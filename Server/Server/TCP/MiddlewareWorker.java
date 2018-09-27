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
    private TCPMiddleware middleware;

    public MiddlewareWorker(Socket socket, TCPMiddleware middleware) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.middleware = middleware;
    }

    private void handleRegistration(ProcedureRequest request) {
        ResourceManagerStub stub = new ResourceManagerStub(this.out, this.in); 
        synchronized (middleware) {
            switch (request.getReserveID()) {
                case 1:
                    this.middleware.registerFlightManagerStub(stub);
                    break;
                case 2:
                    this.middleware.registerCarManagerStub(stub);
                    break;
                case 3:
                    this.middleware.registerRoomManagerStub(stub);
                    break;
                case 4:
                    this.middleware.registerCustomerManagerStub(stub);
                    break;
            }
        }
    }

    public void run() {
        // Read the initial request and determine if this is a consumer or a resource manager 
        try {
            // Initial request
            ProcedureRequest request = (ProcedureRequest) this.in.readObject();

            // Handle resource manager registration
            if (request.getProcedure() == Procedure.RegisterResourceManager) {
                System.out.println("Launched thread to register resource manager.");
                handleRegistration(request);
                this.out.writeObject(new ProcedureResponse(Procedure.RegisterAcknowledge));
            }

            // Handle consumer
            else {
                System.out.println("Launched thread to serve client.");
                ProcedureResponse response = this.middleware.executeRequest(request);  
                this.out.writeObject(response);

                // Continue handling requests
                while (true) {
                    request = (ProcedureRequest) this.in.readObject();
                    response = this.middleware.executeRequest(request);  
                    this.out.writeObject(response);
                }
            }
        } catch(Exception e){
            System.out.println("Something bad happened in a middleware worker thread!!!");
            e.printStackTrace();
        }
    }
}
