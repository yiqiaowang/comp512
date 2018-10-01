package Server.RMI.middleware;

import Server.Common.Services;
import Server.Interface.IResourceManager;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static Server.Common.Services.*;

public class Middleware implements IResourceManager {
    private static final String name = "Middleware";


    private final Map<Services, IResourceManager> resourceManagers;


    public Middleware(Map<Services, IResourceManager> resourceManagers) {
        this.resourceManagers = new HashMap<>(resourceManagers);
    }


    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return resourceManagers.get(FLIGHTS).addFlight(id, flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        return resourceManagers.get(CARS).addCars(id, location, numCars, price);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        return resourceManagers.get(ROOMS).addRooms(id, location, numRooms, price);
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        return resourceManagers.get(CUSTOMERS).newCustomer(id);
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        return resourceManagers.get(CUSTOMERS).newCustomer(id, cid);
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        return resourceManagers.get(FLIGHTS).deleteFlight(id, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        return resourceManagers.get(CARS).deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        return resourceManagers.get(ROOMS).deleteRooms(id, location);
    }

    @Override
    public boolean deleteCustomer(int id, int customerID) throws RemoteException {
        return resourceManagers.get(CUSTOMERS).deleteCustomer(id, customerID);
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        return resourceManagers.get(FLIGHTS).queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        return resourceManagers.get(FLIGHTS).queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        return resourceManagers.get(ROOMS).queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customerID) throws RemoteException {
        return resourceManagers.get(CUSTOMERS).queryCustomerInfo(id, customerID);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return resourceManagers.get(FLIGHTS).queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        return resourceManagers.get(CARS).queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        return resourceManagers.get(ROOMS).queryRoomsPrice(id, location);
    }

    @Override
    public boolean reserveFlight(int id, int customerID, int flightNumber) throws RemoteException {
        return resourceManagers.get(FLIGHTS).reserveFlight(id, customerID, flightNumber);
    }

    @Override
    public boolean reserveCar(int id, int customerID, String location) throws RemoteException {
        return resourceManagers.get(CARS).reserveCar(id, customerID, location);
    }

    @Override
    public boolean reserveRoom(int id, int customerID, String location) throws RemoteException {
        return resourceManagers.get(ROOMS).reserveRoom(id, customerID, location);
    }

    @Override
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
        return false;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
