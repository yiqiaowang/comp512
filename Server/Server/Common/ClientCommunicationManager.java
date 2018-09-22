package Server.Common;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientCommunicationManager {
    
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientCommunicationManager(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    private ProcedureResponse executeProcedure(ProcedureRequest request) throws IOException, ClassNotFoundException {
         out.writeObject(request);
         // BLOCKING PROCEDURE CALL
         ProcedureRequest ack = (ProcedureRequest) in.readObject();
         System.out.println("Got something back!!!!");
         System.out.println(ack.toString());
         // if (ack.somethign...) {
         //     return true;
         // } else {
         //     return false;
         // }
         return new ProcedureResponse(ack.getProcedure());
    };

    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.AddFlight);
        request.setXID(id);
        request.setResourceID(flightNum);
        request.setResourceAmount(flightSeats);
        request.setResourcePrice(flightPrice);
        return this.executeProcedure(request).getBooleanResponse();
    };

    public boolean addCars(int id, String location, int numCars, int price)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.AddCars);
        request.setXID(id);
        request.setLocation(location);
        request.setResourceAmount(numCars);
        request.setResourcePrice(price);
        return this.executeProcedure(request).getBooleanResponse();
    };

    public boolean addRooms(int id, String location, int numRooms, int price)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.AddRooms);
        request.setXID(id);
        request.setLocation(location);
        request.setResourceAmount(numRooms);
        request.setResourcePrice(price);
        return this.executeProcedure(request).getBooleanResponse();
    };
    public int newCustomer(int id)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.AddCustomer);
        request.setXID(id);
        return this.executeProcedure(request).getIntResponse();
    };
    public boolean newCustomer(int id, int customerID)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.AddCustomerID);
        request.setXID(id);
        request.setResourceID(customerID);
        return this.executeProcedure(request).getBooleanResponse();
    };
    public boolean deleteFlight(int id, int flightNum)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.DeleteFlight);
        request.setXID(id);
        request.setResourceID(flightNum);
        return this.executeProcedure(request).getBooleanResponse();
    };
    public boolean deleteCars(int id, String location)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.DeleteCars);
        request.setXID(id);
        request.setLocation(location);
        return this.executeProcedure(request).getBooleanResponse();
    };
    public boolean deleteRooms(int id, String location)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.DeleteRooms);
        request.setXID(id);
        request.setLocation(location);
        return this.executeProcedure(request).getBooleanResponse();
    };
    public boolean deleteCustomer(int id, int customerID)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.DeleteCustomer);
        request.setXID(id);
        request.setResourceID(customerID);
        return this.executeProcedure(request).getBooleanResponse();
    };
    public int queryFlight(int id, int flightNum)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.QueryFlight);
        request.setXID(id);
        request.setResourceID(flightNum);
        return this.executeProcedure(request).getIntResponse();
    };
    public int queryCars(int id, String location)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.QueryCars);
        request.setXID(id);
        request.setLocation(location);
        return this.executeProcedure(request).getIntResponse();
    };
    public int queryRooms(int id, String location)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.QueryRooms);
        request.setXID(id);
        request.setLocation(location);
        return this.executeProcedure(request).getIntResponse();
    };
    public String queryCustomerInfo(int id, int customerID)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.QueryCustomer);
        request.setXID(id);
        request.setResourceID(customerID);
        return this.executeProcedure(request).getStringResponse();
    };
    public int queryFlightPrice(int id, int flightNum)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.QueryCustomer);
        request.setXID(id);
        request.setResourceID(flightNum);
        return this.executeProcedure(request).getIntResponse();
    };
    public int queryCarsPrice(int id, String location)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.QueryCarsPrice);
        request.setXID(id);
        request.setLocation(location);
        return this.executeProcedure(request).getIntResponse();
    };
    public int queryRoomsPrice(int id, String location)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.QueryRoomsPrice);
        request.setXID(id);
        request.setLocation(location);
        return this.executeProcedure(request).getIntResponse();
    };
    public boolean reserveFlight(int id, int customerID, int flightNum)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.ReserveFlight);
        request.setXID(id);
        request.setResourceID(customerID);
        request.setReserveID(flightNum);
        return this.executeProcedure(request).getBooleanResponse();
    };
    public boolean reserveCar(int id, int customerID, String location)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.ReserveCar);
        request.setXID(id);
        request.setResourceID(customerID);
        request.setLocation(location);
        return this.executeProcedure(request).getBooleanResponse();
    };
    public boolean reserveRoom(int id, int customerID, String location)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.ReserveRoom);
        request.setXID(id);
        request.setResourceID(customerID);
        request.setLocation(location);
        return this.executeProcedure(request).getBooleanResponse();
    };

    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room)throws IOException, ClassNotFoundException {
        ProcedureRequest request = new ProcedureRequest(Procedure.Bundle);
        request.setXID(id);
        request.setResourceID(customerID);
        request.setResourceIDs(flightNumbers);
        request.setLocation(location);
        request.setRequireCar(car);
        request.setRequireRoom(room);
        return this.executeProcedure(request).getBooleanResponse();
    };
}
