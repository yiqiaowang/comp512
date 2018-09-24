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

    public void registerResourceManager(ProcedureRequest request) throws IOException {
        if (request.getProcedure() == Procedure.RegisterResourceManager) {
            switch ( request.getReserveID() ) {
                case 1:
                    this.flightManagerStub = new ResourceManagerStub(
                            request.getLocation(),
                            request.getResourceID()
                            ); 
                    break;
                case 2:
                    this.carManagerStub = new ResourceManagerStub(
                            request.getLocation(),
                            request.getResourceID()
                            ); 
                    break;
                case 3:
                    this.roomManagerStub = new ResourceManagerStub(
                            request.getLocation(),
                            request.getResourceID()
                            ); 
                    break;
                case 4:
                    this.customerManagerStub = new ResourceManagerStub(
                            request.getLocation(),
                            request.getResourceID()
                            ); 
                    break;
            }
        }
    };

    // public ProcedureResponse executeRequest(ProcedureRequest request) {
    //     Procedure procedure = request.getProcedure();  
    //     ProcedureResponse response;
        
    //     switch (procedure) {
    //         case AddFlight:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     addFlight(
    //                         request.getXID(),
    //                         request.getResourceID(),
    //                         request.getResourceAmount(),
    //                         request.getResourcePrice()
    //                         )
    //                     );
    //             break;
    //         case AddCars:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     addCars(
    //                         request.getXID(),
    //                         request.getLocation(),
    //                         request.getResourceAmount(),
    //                         request.getResourcePrice()
    //                         )
    //                     );
    //             break;
    //         case AddRooms:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     addRooms(
    //                         request.getXID(),
    //                         request.getLocation(),
    //                         request.getResourceAmount(),
    //                         request.getResourcePrice()
    //                         )
    //                     );
    //             break;
    //         case AddCustomer:
    //             response = new ProcedureResponse(procedure);
    //             response.setIntResponse( newCustomer( request.getXID()));
    //             break;
    //         case AddCustomerID:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     newCustomer(
    //                         request.getXID(),
    //                         request.getResourceID()
    //                         )
    //                     );
    //             break;
    //         case DeleteFlight:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     deleteFlight(
    //                         request.getXID(),
    //                         request.getResourceID()
    //                         )
    //                     );
    //             break;
    //         case DeleteCars: response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     deleteCars(
    //                         request.getXID(),
    //                         request.getLocation()
    //                         )
    //                     );
    //             break;
    //         case DeleteRooms:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     deleteRooms(
    //                         request.getXID(),
    //                         request.getLocation()
    //                         )
    //                     );
    //             break;
    //         case DeleteCustomer:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     deleteFlight(
    //                         request.getXID(),
    //                         request.getResourceID()
    //                         )
    //                     );
    //             break;
    //         case QueryFlight:
    //             response = new ProcedureResponse(procedure);
    //             response.setIntResponse(
    //                     queryFlight(
    //                         request.getXID(),
    //                         request.getResourceID()
    //                         )
    //                     );
    //             break;
    //         case QueryCars:
    //             response = new ProcedureResponse(procedure);
    //             response.setIntResponse(
    //                     queryCars(
    //                         request.getXID(),
    //                         request.getLocation()
    //                         )
    //                     );
    //             break;
    //         case QueryRooms:
    //             response = new ProcedureResponse(procedure);
    //             response.setIntResponse(
    //                     queryRooms(
    //                         request.getXID(),
    //                         request.getLocation()
    //                         )
    //                     );
    //             break;
    //         case QueryCustomer:
    //             response = new ProcedureResponse(procedure);
    //             response.setStringResponse(
    //                     queryCustomerInfo(
    //                         request.getXID(),
    //                         request.getResourceID()
    //                         )
    //                     );
    //             break;
    //         case QueryFlightPrice:
    //             response = new ProcedureResponse(procedure);
    //             response.setIntResponse(
    //                     queryFlightPrice(
    //                         request.getXID(),
    //                         request.getResourceID()
    //                         )
    //                     );
    //             break;
    //         case QueryCarsPrice:
    //             response = new ProcedureResponse(procedure);
    //             response.setIntResponse(
    //                     queryCarsPrice(
    //                         request.getXID(),
    //                         request.getLocation()
    //                         )
    //                     );
    //             break;
    //         case QueryRoomsPrice:
    //             response = new ProcedureResponse(procedure);
    //             response.setIntResponse(
    //                     queryRoomsPrice(
    //                         request.getXID(),
    //                         request.getLocation()
    //                         )
    //                     );
    //             break;
    //         case ReserveFlight:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     reserveItem(
    //                         request.getXID(),
    //                         request.getResourceID(),
    //                         Flight.getKey(request.getReserveID()),
    //                         String.valueOf(request.getReserveID())
    //                         )
    //                     );
    //             break;
    //         case ReserveCar:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     reserveItem(
    //                         request.getXID(),
    //                         request.getResourceID(),
    //                         Car.getKey(request.getLocation()),
    //                         request.getLocation()
    //                         )
    //                     );
    //             break;
    //         case ReserveRoom:
    //             response = new ProcedureResponse(procedure);
    //             response.setBooleanResponse(
    //                     reserveItem(
    //                         request.getXID(),
    //                         request.getResourceID(),
    //                         Room.getKey(request.getLocation()),
    //                         request.getLocation()
    //                         )
    //                     );
    //             break;
    //         case Bundle:
    //             response = new ProcedureResponse(procedure);
    //             break;
    //         default:
    //             response = new ProcedureResponse(Procedure.Error);
    //     }

    //     return response;
    // }

    public void start() throws IOException {
        this.middlewareSocket = new ServerSocket(TCPMiddleware.middlewarePort);
    }

    public void stop() throws IOException {
        this.middlewareSocket.close();
    }
}
