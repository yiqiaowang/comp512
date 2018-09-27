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
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;

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
        ProcedureResponse response = new ProcedureResponse(Procedure.Error);
        
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
                response = this.reserveItem(request, this.flightManagerStub, Procedure.DecrementFlightsAvailable, Procedure.AddFlightReservation, Procedure.IncrementFlightsAvailable);
                break;
            case ReserveCar:
                response = this.reserveItem(request, this.carManagerStub, Procedure.DecrementCarsAvailable, Procedure.AddCarReservation, Procedure.IncrementCarsAvailable);
                break;
            case ReserveRoom:
                response = this.reserveItem(request, this.roomManagerStub, Procedure.DecrementRoomsAvailable, Procedure.AddRoomReservation, Procedure.IncrementRoomsAvailable);
                break;
            case Bundle:
                response = this.reserveBundle(request);
                break;
            default:
                response = new ProcedureResponse(Procedure.Error);
        }

        return response;
    }

    private ProcedureResponse reserveBundle(ProcedureRequest request) throws IOException, ClassNotFoundException {
        int roomPrice = 0;
        int carPrice = 0;

        ProcedureResponse response = new ProcedureResponse(request.getProcedure());
        response.setBooleanResponse(false);

        // Figure out prices for flights
        Map<String, Integer> flightPriceMap = new HashMap<>();
        Vector<String> flightIDs = request.getResourceIDs();

        ProcedureRequest subReq = new ProcedureRequest();
        subReq.setXID(request.getXID());
        subReq.setProcedure(Procedure.DecrementFlightsAvailable);

        for (String flight : flightIDs) {
            subReq.setReserveID(Integer.parseInt(flight));
            int price = this.flightManagerStub.executeRemoteProcedure(subReq).getIntResponse();

            if (price == -1) {
                System.out.println("Something bad happened. Could not book flight in bundle!!!");
                return response; 
            } else {
                flightPriceMap.put(flight, Integer.valueOf(price));
            }
        }
        

        // Query for car price if necessary
        if (request.getRequireCar()) {
            System.out.println("Bundle requires car");
            request.setProcedure(Procedure.DecrementCarsAvailable);
            carPrice = this.carManagerStub.executeRemoteProcedure(request).getIntResponse();

            if (carPrice == -1) {
                System.out.println("Something bad happened. Could not book car in bundle!!!");
                return response;
            }
        }

        // Query for room price
        if (request.getRequireRoom()) {
            System.out.println("Bundle requires room");
            request.setProcedure(Procedure.DecrementRoomsAvailable);
            roomPrice = this.roomManagerStub.executeRemoteProcedure(request).getIntResponse();

            if (roomPrice == -1) {
                System.out.println("Something bad happened. Could not book addRooms in bundle!!!");
                return response;
            }

        }

        // Calculate total customer bill
        ProcedureRequest customerReq = new ProcedureRequest();
        customerReq.setXID(request.getXID());
        customerReq.setResourceID(request.getResourceID());

        if (carPrice > 0) {
            System.out.println("Booking car for: " + carPrice);

            customerReq.setProcedure(Procedure.AddCarReservation);
            customerReq.setLocation(request.getLocation());
            customerReq.setResourcePrice(carPrice);

            System.out.println("Subreq procedure is: " + customerReq.getProcedure());
            this.customerManagerStub.executeRemoteProcedure(customerReq);
        }

        if (roomPrice > 0) {
            System.out.println("Booking room for: " + roomPrice);

            customerReq.setProcedure(Procedure.AddRoomReservation);
            customerReq.setLocation(request.getLocation());
            customerReq.setResourcePrice(roomPrice);

            System.out.println("Subreq procedure is: " + customerReq.getProcedure());
            this.customerManagerStub.executeRemoteProcedure(subReq);
        }

        for (String flight : flightPriceMap.keySet()) {
            System.out.println("Booking flight: " + flight);

            customerReq.setProcedure(Procedure.AddFlightReservation);
            customerReq.setReserveID(Integer.parseInt(flight));
            customerReq.setResourcePrice(flightPriceMap.get(flight));

            System.out.println("Customer Request is: " + customerReq.toString());
            this.customerManagerStub.executeRemoteProcedure(customerReq);
        }


        response.setBooleanResponse(true);
        return response;
    }

    private boolean bundleRollback(int xid, Vector<String> flightIDs, String location, boolean car, boolean room) throws IOException, ClassNotFoundException {
        ProcedureRequest rollback = new ProcedureRequest();
        boolean rollbackSuccess;
        rollback.setXID(xid);

        if ( car ) {
            rollback.setProcedure(Procedure.IncrementCarsAvailable); 
            rollback.setLocation(location);
            rollbackSuccess = this.carManagerStub.executeRemoteProcedure(rollback).getBooleanResponse();

            if (!rollbackSuccess){
                System.out.println("Something bad happened, could not roll back"); 
                return false;
            }
        }

        if ( room ) {
            rollback.setProcedure(Procedure.IncrementRoomsAvailable); 
            rollback.setLocation(location);
            rollbackSuccess = this.roomManagerStub.executeRemoteProcedure(rollback).getBooleanResponse();

            if (!rollbackSuccess){
                System.out.println("Something bad happened, could not roll back"); 
                return false;
            }
        }

        // Flights
        rollback.setProcedure(Procedure.BatchIncrementFlightsAvailable);
        rollback.setResourceIDs(flightIDs);

        rollbackSuccess = this.flightManagerStub.executeRemoteProcedure(rollback).getBooleanResponse();

        if (! rollbackSuccess) {
            System.out.println("Something bad happened, could not roll back"); 
            return false;
        }

        return true;
    }

    private ProcedureResponse reserveItem(ProcedureRequest request, ResourceManagerStub stub, Procedure decrementProcedure, Procedure clientProcedure, Procedure incrementProcedure) throws IOException, ClassNotFoundException {
        request.setProcedure(decrementProcedure);
        int price = stub.executeRemoteProcedure(request).getIntResponse();
        ProcedureResponse response = new ProcedureResponse(Procedure.Error);
        response.setBooleanResponse(false);

        if (price != -1) {
            request.setProcedure(clientProcedure);
            request.setResourcePrice(price);
            response = this.customerManagerStub.executeRemoteProcedure(request);

            if (!response.getBooleanResponse()) {
                // Roll back        
                request.setProcedure(incrementProcedure);
                boolean rollbackSuccess = stub.executeRemoteProcedure(request).getBooleanResponse();  

                // set response as failed
                response.setBooleanResponse(false);

                // Failed rollback :(
                if (! rollbackSuccess){
                    System.out.println("Something very bad happened. Failed to roll back!");
                }
            }
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
