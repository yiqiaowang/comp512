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
    private Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    public TCPResourceManager(String name) {
        super(name);
    }

    public void start(String server, int port) throws IOException, ClassNotFoundException {
        this.socket = new Socket(server, port);
    }

    public void setupStreams() throws IOException {
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
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
            // case ReserveFlight:
            //     response = new ProcedureResponse(procedure);
            //     response.setBooleanResponse(
            //             reserveItem(
            //                 request.getXID(),
            //                 request.getResourceID(),
            //                 Flight.getKey(request.getReserveID()),
            //                 String.valueOf(request.getReserveID())
            //                 )
            //             );
            //     break;
            // case ReserveCar:
            //     response = new ProcedureResponse(procedure);
            //     response.setBooleanResponse(
            //             reserveItem(
            //                 request.getXID(),
            //                 request.getResourceID(),
            //                 Car.getKey(request.getLocation()),
            //                 request.getLocation()
            //                 )
            //             );
            //     break;
            // case ReserveRoom:
            //     response = new ProcedureResponse(procedure);
            //     response.setBooleanResponse(
            //             reserveItem(
            //                 request.getXID(),
            //                 request.getResourceID(),
            //                 Room.getKey(request.getLocation()),
            //                 request.getLocation()
            //                 )
            //             );
            //     break;

            case DecrementFlightsAvailable:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        decrementItemsAvailable(
                            request.getXID(),
                            Flight.getKey(request.getReserveID()),
                            String.valueOf(request.getReserveID())
                            )
                        );
                break;
            case IncrementFlightsAvailable:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        incrementItemsAvailable(
                            request.getXID(),
                            Flight.getKey(request.getReserveID()),
                            String.valueOf(request.getReserveID())
                            )
                        );
                break;
            case DecrementRoomsAvailable:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        decrementItemsAvailable(
                            request.getXID(),
                            Room.getKey(request.getLocation()),
                            request.getLocation()
                            )
                        );
                break;
            case IncrementRoomsAvailable:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        incrementItemsAvailable(
                            request.getXID(),
                            Room.getKey(request.getLocation()),
                            request.getLocation()
                            )
                        );
                break;
            case DecrementCarsAvailable:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        decrementItemsAvailable(
                            request.getXID(),
                            Car.getKey(request.getLocation()),
                            request.getLocation()
                            )
                        );
                break;
            case IncrementCarsAvailable:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        incrementItemsAvailable(
                            request.getXID(),
                            Car.getKey(request.getLocation()),
                            request.getLocation()
                            )
                        );
                break;
            case BatchDecrementFlightsAvailable:
                response = new ProcedureResponse(procedure);
                response.setIntResponse(
                        batchDecrementFlightsAvailable(
                            request.getXID(),
                            request.getResourceIDs()
                            )
                        );
                break;
            case BatchIncrementFlightsAvailable:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        batchIncrementFlightsAvailable(
                            request.getXID(),
                            request.getResourceIDs()
                            )
                        );
                break;
            case AddRoomReservation:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        addCustomerReservation(
                            request.getXID(),
                            request.getResourceID(),
                            Room.getKey(request.getLocation()),
                            request.getLocation(),
                            request.getResourcePrice()
                            )
                        );
                break;
            case AddCarReservation:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        addCustomerReservation(
                            request.getXID(),
                            request.getResourceID(),
                            Car.getKey(request.getLocation()),
                            request.getLocation(),
                            request.getResourcePrice()
                            )
                        );
                break;
            case AddFlightReservation:
                response = new ProcedureResponse(procedure);
                response.setBooleanResponse(
                        addCustomerReservation(
                            request.getXID(),
                            request.getResourceID(),
                            Flight.getKey(request.getReserveID()),
                            String.valueOf(request.getReserveID()),
                            request.getResourcePrice()
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
        this.out.writeObject(response);
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
