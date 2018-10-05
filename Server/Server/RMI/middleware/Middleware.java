package Server.RMI.middleware;

import Server.Common.Car;
import Server.Common.Flight;
import Server.Common.Room;
import Server.Interface.IResourceManager;

import java.rmi.RemoteException;
import java.util.Vector;

public class Middleware implements IResourceManager {
    private static final String name = "Middleware";


    private final IResourceManager flightsResourceManager;
    private final IResourceManager carsResourceManager;
    private final IResourceManager roomsResourceManager;

    private final ICustomerResourceManager customersResourceManager;


    public Middleware(IResourceManager flightsResourceManager, IResourceManager carsResourceManager, IResourceManager roomsResourceManager, ICustomerResourceManager customersResourceManager) {
        this.flightsResourceManager = flightsResourceManager;
        this.roomsResourceManager = roomsResourceManager;
        this.carsResourceManager = carsResourceManager;
        this.customersResourceManager = customersResourceManager;
    }



    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return flightsResourceManager.addFlight(id, flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        return carsResourceManager.addCars(id, location, numCars, price);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        return roomsResourceManager.addRooms(id, location, numRooms, price);
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        return customersResourceManager.newCustomer(id);
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        return customersResourceManager.newCustomer(id, cid);
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        return flightsResourceManager.deleteFlight(id, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        return carsResourceManager.deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        return roomsResourceManager.deleteRooms(id, location);
    }

    @Override
    public boolean deleteCustomer(int id, int customerID) throws RemoteException {
        return customersResourceManager.deleteCustomer(id, customerID);
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        return flightsResourceManager.queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        return carsResourceManager.queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        return roomsResourceManager.queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customerID) throws RemoteException {
        return customersResourceManager.queryCustomerInfo(id, customerID);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return flightsResourceManager.queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        return carsResourceManager.queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        return roomsResourceManager.queryRoomsPrice(id, location);
    }

    @Override
    public boolean reserveFlight(int id, int customerID, int flightNumber) throws RemoteException {
        String customerInfo = customersResourceManager.queryCustomerInfo(id, customerID);

        if (customerInfo == null || customerInfo.equals("")) {
            // customer does not exist
            return false;
        }

        boolean flightReserved = flightsResourceManager.reserveFlight(id, customerID, flightNumber);
        if (!flightReserved) {
            return false;
        }

        int flightPrice = flightsResourceManager.queryFlightPrice(id, flightNumber);

        customersResourceManager.reserveCustomer(customerID, Flight.getKey(flightNumber), String.valueOf(flightNumber), flightPrice);

        return true;
    }

    @Override
    public boolean reserveCar(int id, int customerID, String location) throws RemoteException {
        String customerInfo = customersResourceManager.queryCustomerInfo(id, customerID);

        if (customerInfo == null || customerInfo.equals("")) {
            // customer does not exist
            return false;
        }

        boolean carReserved = carsResourceManager.reserveCar(id, customerID, location);
        if (!carReserved) {
            return false;
        }

        int rentalPrice = flightsResourceManager.queryCarsPrice(id, location);

        customersResourceManager.reserveCustomer(customerID, Car.getKey(location), location, rentalPrice);

        return true;
    }

    @Override
    public boolean reserveRoom(int id, int customerID, String location) throws RemoteException {
        String customerInfo = customersResourceManager.queryCustomerInfo(id, customerID);

        if (customerInfo == null || customerInfo.equals("")) {
            // customer does not exist
            return false;
        }

        boolean roomReserved = roomsResourceManager.reserveRoom(id, customerID, location);
        if (!roomReserved) {
            return false;
        }

        int roomPrice = roomsResourceManager.queryRoomsPrice(id, location);

        customersResourceManager.reserveCustomer(customerID, Room.getKey(location), location, roomPrice);

        return true;
    }

    @Override
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
        if (flightNumbers == null || flightNumbers.isEmpty()) {
            // there must be at least one flight
            return false;
        }

        for (String flightNumber : flightNumbers) {
            boolean reservedFlight = flightsResourceManager.reserveFlight(id, customerID, Integer.parseInt(flightNumber));
            if (!reservedFlight) {
                return false;
            }
        }

        if (car) {
            boolean reservedCar = carsResourceManager.reserveCar(id, customerID, location);
            if (!reservedCar) {
                return false;
            }
        }
        if (room) {
            boolean reservedRoom = roomsResourceManager.reserveRoom(id, customerID, location);
            if (!reservedRoom) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
