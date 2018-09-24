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

// import Server.Common.FlightManagerStub;
// import Server.Common.CarManagerStub;
// import Server.Common.RoomManagerStub;
// import Server.Common.CustomerManagerStub;

import java.net.*;
import java.io.*;


public class TCPMiddleware
{

    private static String serverName = "TCPMiddleware";

    private static int middlewarePort = 6666;
    private ServerSocket middlewareSocket;

    // Clients of the middleware, includes ResourceManagers and consumers of the service
    // private static int clientPort = 6666;
    // private ServerSocket ServerSocket;
    // private Socket clientSocket;
    // private ObjectOutputStream clientOut;
    // private ObjectInputStream clientIn;

    // Stubs for the resource managers to register for
    private ResourceManagerStub flightManagerStub;
    private ResourceManagerStub carManagerStub;
    private ResourceManagerStub roomManagerStub;
    private ResourceManagerStub customerManagerStub;

    // NOTE: each stub resource manager needs its own ...
    // private Socket rmSocket;
    // private ObjectOutputStream rmOut;
    // private ObjectInputStream rmIn;

    public static void main(String args[])
    {
        try {
            // Create a new middleware object
            TCPMiddleware middleware = new TCPMiddleware();
            middleware.start();
            System.out.println("Started middlware. Awaiting connections");

            while (true) {
                MiddlewareWorker w = new MiddlewareWorker(middleware.acceptConnection(), middleware);
                Thread t = new Thread(w);
                t.start();
            }
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mMiddleware exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
    }

    public Socket acceptConnection() throws IOException { 
        return this.middlewareSocket.accept(); 
    }

    public void registerCarManagerStub(ResourceManagerStub stub) {
        this.carManagerStub = stub;
    } 

    public void registerFlightManagerStub(ResourceManagerStub stub) {
        this.flightManagerStub = stub;
    } 

    public void registerRoomManagerStub(ResourceManagerStub stub) {
        this.roomManagerStub = stub;
    } 

    public void registerCustomerManagerStub(ResourceManagerStub stub) {
        this.customerManagerStub = stub;
    } 

    public ProcedureResponse executeRequest(ProcedureRequest request) throws IOException, ClassNotFoundException {
        Procedure procedure = request.getProcedure();  
        ProcedureResponse response;
        
        switch (procedure) {
            case AddFlight:
                response = this.flightManagerStub.executeRemoteProcedure(request);
                break;
            case AddCars:
                response = this.carManagerStub.executeRemoteProcedure(request);
                break;
            case AddRooms:
                response = this.roomManagerStub.executeRemoteProcedure(request);
                break;
            case AddCustomer:
                response = this.customerManagerStub.executeRemoteProcedure(request);
                break;
            case AddCustomerID:
                response = this.customerManagerStub.executeRemoteProcedure(request);
                break;
            case DeleteFlight:
                response = this.flightManagerStub.executeRemoteProcedure(request);
                break;
            case DeleteCars:
                response = this.carManagerStub.executeRemoteProcedure(request);
                break;
            case DeleteRooms:
                response = this.roomManagerStub.executeRemoteProcedure(request);
                break;
            case DeleteCustomer:
                response = this.customerManagerStub.executeRemoteProcedure(request);
                break;
            case QueryFlight:
                response = this.flightManagerStub.executeRemoteProcedure(request);
                break;
            case QueryCars:
                response = this.carManagerStub.executeRemoteProcedure(request);
                break;
            case QueryRooms:
                response = this.roomManagerStub.executeRemoteProcedure(request);
                break;
            case QueryCustomer:
                response = this.customerManagerStub.executeRemoteProcedure(request);
                break;
            case QueryFlightPrice:
                response = this.flightManagerStub.executeRemoteProcedure(request);
                break;
            case QueryCarsPrice:
                response = this.carManagerStub.executeRemoteProcedure(request);
                break;
            case QueryRoomsPrice:
                response = this.roomManagerStub.executeRemoteProcedure(request);
                break;
            case ReserveFlight:
                response = this.flightManagerStub.executeRemoteProcedure(request);
                break;
            case ReserveCar:
                response = this.carManagerStub.executeRemoteProcedure(request);
                break;
            case ReserveRoom:
                response = this.roomManagerStub.executeRemoteProcedure(request);
                break;
            case Bundle:
                response = new ProcedureResponse(procedure);
                break;
            default:
                response = new ProcedureResponse(Procedure.Error);
        }

        return response;
    }

    public void start() throws IOException {
        this.middlewareSocket = new ServerSocket(TCPMiddleware.middlewarePort);
    }

    public void stop() throws IOException {
        this.middlewareSocket.close();
    }
}
