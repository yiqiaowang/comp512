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
            server.acceptConnection();
            server.setupStreams();
            ProcedureRequest request = server.receiveRequest();
            ProcedureResponse response = server.executeRequest(request);
            server.sendResponse(response);
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
        this.serverSocket = new ServerSocket(port);
    }

    public void acceptConnection() throws IOException {
        this.clientSocket = this.serverSocket.accept();
    }

    public void setupStreams() throws IOException {
        this.out = new ObjectOutputStream(this.clientSocket.getOutputStream());
        this.in = new ObjectInputStream(this.clientSocket.getInputStream());
    }

    public ProcedureRequest receiveRequest() throws IOException, ClassNotFoundException {
        return (ProcedureRequest) this.in.readObject();
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
        }

        return response;
    }

    public void sendResponse(ProcedureResponse response) throws IOException {
        this.out.writeObject(response);
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
