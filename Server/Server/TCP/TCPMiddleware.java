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

// import Server.Common.FlightResourceManager;
// import Server.Common.CarResourceManager;
// import Server.Common.RoomResourceManager;
// import Server.Common.CustomerResourceManager;

import java.net.*;
import java.io.*;


public class TCPMiddleware
{

    private static String serverName = "TCPMiddleware";

    private static int middlewarePort = 6666;
    private ServerSocket middlewareSocket;

    // Client
    private static int clientPort = 6666;
    private ServerSocket clientServerSocket;
    private Socket clientSocket;
    private ObjectOutputStream clientOut;
    private ObjectInputStream clientIn;

    // ResourceManager
    private static int resourceManagerPort = 6667;
    private ServerSocket resourceManagerServerSocket;
    private FlightManager flightManager;
    private CarManager carManager;
    private RoomManager roomManager;
    private CustomerManager customerManager;

    // NOTE: each stub resource manager needs its own ...
    // private Socket rmSocket;
    // private ObjectOutputStream rmOut;
    // private ObjectInputStream rmIn;

    public static void main(String args[])
    {
        try {
            // Create a new Server object
            TCPMiddleware middleware = new TCPMiddleware();
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

    public void registerResourceManager(ProcedureRequest request) {
        if (request.getProcedure() == Procedure.RegisterResourceManager) {
            switch ( request.getReserveID() ) {
                case 1:
                    this.flightManager = new FlightResourceManager(
                            request.getLocation(),
                            request.getResourceID()
                            ); 
                    break;
                case 2:
                    this.carManager = new CarResourceManager(
                            request.getLocation(),
                            request.getResourceID()
                            ); 
                    break;
                case 3:
                    this.roomManager = new RoomResourceManager(
                            request.getLocation(),
                            request.getResourceID()
                            ); 
                    break;
                case 4:
                    this.customerManager = new CustomerResourceManager(
                            request.getLocation(),
                            request.getResourceID()
                            ); 
                    break;
            }
        }
    };

    public void startClient(int port) throws IOException, ClassNotFoundException {
        this.clientServerSocket = new ServerSocket(port);
    }

    public void startResourceManager(int port) throws IOException, ClassNotFoundException {
        this.resourceManagerServerSocket = new ServerSocket(port);
    }

    public void acceptClientConnection() throws IOException {
        this.clientSocket = this.clientServerSocket.accept();
    }

    public void acceptResourceManagerConnection() throws IOException {
        this.clientSocket = this.resourceManagerServerSocket.accept();
    }

    public void setupClientStreams() throws IOException {
        this.clientOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
        this.clientIn = new ObjectInputStream(this.clientSocket.getInputStream());
    }

    public ProcedureRequest receiveRequest() throws IOException, ClassNotFoundException {
        return (ProcedureRequest) this.clientIn.readObject();
    }

    public ProcedureResponse executeRequest(ProcedureRequest request) {
        Procedure procedure = request.getProcedure();  
        ProcedureResponse response;
        
        switch (procedure) {
            case AddFlight:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        addFlight(
                            request.getXID(),
                            request.getResourceID(),
                            request.getResourceAmount(),
                            request.getResourcePrice()
                            )
                        );
                break;
            case AddCars:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        addCars(
                            request.getXID(),
                            request.getLocation(),
                            request.getResourceAmount(),
                            request.getResourcePrice()
                            )
                        );
                break;
            case AddRooms:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        addRooms(
                            request.getXID(),
                            request.getLocation(),
                            request.getResourceAmount(),
                            request.getResourcePrice()
                            )
                        );
                break;
            case AddCustomer:
                response = new ProcedureResponse(procedure);
                response.setIntResponse( newCustomer( request.getXID()));
                break;
            case AddCustomerID:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        newCustomer(
                            request.getXID(),
                            request.getResourceID()
                            )
                        );
                break;
            case DeleteFlight:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        deleteFlight(
                            request.getXID(),
                            request.getResourceID()
                            )
                        );
                break;
            case DeleteCars:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        deleteCars(
                            request.getXID(),
                            request.getLocation()
                            )
                        );
                break;
            case DeleteRooms:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        deleteRooms(
                            request.getXID(),
                            request.getLocation()
                            )
                        );
                break;
            case DeleteCustomer:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        deleteFlight(
                            request.getXID(),
                            request.getResourceID()
                            )
                        );
                break;
            case QueryFlight:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        queryFlight(
                            request.getXID(),
                            request.getResourceID()
                            )
                        );
                break;
            case QueryCars:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        queryCars(
                            request.getXID(),
                            request.getLocation()
                            )
                        );
                break;
            case QueryRooms:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        queryRooms(
                            request.getXID(),
                            request.getLocation()
                            )
                        );
                break;
            case QueryCustomer:
                response = new ProcedureResponse(procedure);
                response.setStringResponse(
                        queryCustomerInfo(
                            request.getXID(),
                            request.getResourceID()
                            )
                        );
                break;
            case QueryFlightPrice:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        queryFlightPrice(
                            request.getXID(),
                            request.getResourceID()
                            )
                        );
                break;
            case QueryCarsPrice:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        queryCarsPrice(
                            request.getXID(),
                            request.getLocation()
                            )
                        );
                break;
            case QueryRoomsPrice:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        queryRoomsPrice(
                            request.getXID(),
                            request.getLocation()
                            )
                        );
                break;
            case ReserveFlight:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        reserveItem(
                            request.getXID(),
                            request.getResourceID(),
                            Flight.getKey(request.getReserveID()),
                            String.valueOf(request.getReserveID())
                            )
                        );
                break;
            case ReserveCar:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        reserveItem(
                            request.getXID(),
                            request.getResourceID(),
                            Car.getKey(request.getLocation()),
                            request.getLocation()
                            )
                        );
                break;
            case ReserveRoom:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        reserveItem(
                            request.getXID(),
                            request.getResourceID(),
                            Room.getKey(request.getLocation()),
                            request.getLocation()
                            )
                        );
                break;
            case Bundle:
                response = new ProcedureResponse(procedure);
                break;
            default:
                response = new ProcedureResponse(Procedure.Error);
        }

        return response;
    }

    public void sendResponse(ProcedureResponse response) throws IOException {
        this.clientOut.writeObject(response);
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
