package Server.RMI.middleware;

import Server.Common.Car;
import Server.Common.Flight;
import Server.Common.Room;
import Server.Interface.IResourceManager;
import Server.Interface.InvalidTransactionException;
import Server.RMI.RMIMiddleware;
import Server.Transaction.ResourceLockRequest;
import Server.Transaction.TransactionManager;

import java.rmi.RemoteException;
import java.util.Vector;

import static Server.Common.Services.*;
import static Server.LockManager.TransactionLockObject.LockType.LOCK_READ;
import static Server.LockManager.TransactionLockObject.LockType.LOCK_WRITE;

public class Middleware implements IResourceManager {
    private static final String name = "Middleware";


    private final IResourceManager flightsResourceManager;
    private final IResourceManager carsResourceManager;
    private final IResourceManager roomsResourceManager;

    private final ICustomerResourceManager customersResourceManager;
    private final TransactionManager transactionManager = new TransactionManager();


    public Middleware(IResourceManager flightsResourceManager, IResourceManager carsResourceManager, IResourceManager roomsResourceManager, ICustomerResourceManager customersResourceManager) {
        this.flightsResourceManager = flightsResourceManager;
        this.roomsResourceManager = roomsResourceManager;
        this.carsResourceManager = carsResourceManager;
        this.customersResourceManager = customersResourceManager;
    }



    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(FLIGHTS.toString(), flightsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            System.out.println("Failed to acquire the locks");
            throw new InvalidTransactionException(id);
        }
        try {
            return flightsResourceManager.addFlight(id, flightNum, flightSeats, flightPrice);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CARS.toString(), carsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return carsResourceManager.addCars(id, location, numCars, price);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(ROOMS.toString(), roomsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return roomsResourceManager.addRooms(id, location, numRooms, price);
        } catch (RemoteException | InvalidTransactionException e) {
                transactionManager.abort(id);
                throw e;
        }
    }

    @Override
    public int newCustomer(int id) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CUSTOMERS.toString(), customersResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return customersResourceManager.newCustomer(id);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CUSTOMERS.toString(), customersResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return customersResourceManager.newCustomer(id, cid);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(FLIGHTS.toString(), flightsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return flightsResourceManager.deleteFlight(id, flightNum);
        } catch (RemoteException | InvalidTransactionException e) {
                transactionManager.abort(id);
                throw e;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CARS.toString(), carsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return carsResourceManager.deleteCars(id, location);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(ROOMS.toString(), roomsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return roomsResourceManager.deleteRooms(id, location);
        } catch (RemoteException | InvalidTransactionException e) {
                transactionManager.abort(id);
                throw e;
        }
    }

    @Override
    public boolean deleteCustomer(int id, int customerID) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CUSTOMERS.toString(), customersResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return customersResourceManager.deleteCustomer(id, customerID);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(FLIGHTS.toString(), flightsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return flightsResourceManager.queryFlight(id, flightNumber);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CARS.toString(), carsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return carsResourceManager.queryCars(id, location);
        } catch (RemoteException | InvalidTransactionException e) {
                transactionManager.abort(id);
                throw e;
        }
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(ROOMS.toString(), roomsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return roomsResourceManager.queryRooms(id, location);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customerID) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CUSTOMERS.toString(), customersResourceManager, LOCK_READ));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return customersResourceManager.queryCustomerInfo(id, customerID);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(FLIGHTS.toString(), flightsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return flightsResourceManager.queryFlightPrice(id, flightNumber);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CARS.toString(), carsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }
        try {
            return carsResourceManager.queryCarsPrice(id, location);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(ROOMS.toString(), roomsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }

        try {
            return roomsResourceManager.queryRoomsPrice(id, location);
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customerID, int flightNumber) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id,
                new ResourceLockRequest(CUSTOMERS.toString(), customersResourceManager, LOCK_WRITE),
                new ResourceLockRequest(FLIGHTS.toString(), flightsResourceManager, LOCK_WRITE));

        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }

        try {
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
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean reserveCar(int id, int customerID, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id,
                new ResourceLockRequest(CUSTOMERS.toString(), customersResourceManager, LOCK_WRITE),
                new ResourceLockRequest(CARS.toString(), carsResourceManager, LOCK_WRITE));

        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }

        try {

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
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customerID, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id,
                new ResourceLockRequest(CUSTOMERS.toString(), customersResourceManager, LOCK_WRITE),
                new ResourceLockRequest(ROOMS.toString(), roomsResourceManager, LOCK_WRITE));

        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }

        try {

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
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException, InvalidTransactionException {
        if (flightNumbers == null || flightNumbers.isEmpty()) {
            // there must be at least one flight
            return false;
        }


        boolean locksAcquired = transactionManager.requestLocksOnResources(id,
                new ResourceLockRequest(CUSTOMERS.toString(), customersResourceManager, LOCK_WRITE),
                new ResourceLockRequest(FLIGHTS.toString(), flightsResourceManager, LOCK_WRITE));

        if (!locksAcquired) {
            throw new InvalidTransactionException(id);
        }

        if (car && !transactionManager.requestLocksOnResources(id,
                    new ResourceLockRequest(CARS.toString(), carsResourceManager, LOCK_WRITE))) {
            throw new InvalidTransactionException(id);
        }

        if (room && !transactionManager.requestLocksOnResources(id,
                new ResourceLockRequest(ROOMS.toString(), roomsResourceManager, LOCK_WRITE))) {
            throw new InvalidTransactionException(id);
        }

        try {

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
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public int start() throws RemoteException {
        return transactionManager.startTransaction();
    }

    @Override
    public boolean start(int xid) throws RemoteException {
        return transactionManager.startTransaction(xid);
    }

    @Override
    public void abort(int xid) throws RemoteException {
        transactionManager.abort(xid);
    }

    @Override
    public boolean commit(int xid) throws RemoteException {
        return transactionManager.commit(xid);
    }

    @Override
    public boolean shutdown() throws RemoteException {
        boolean allShutdown = true;

        for (IResourceManager resourceManager: new IResourceManager[] {flightsResourceManager, carsResourceManager, roomsResourceManager, customersResourceManager}) {
            allShutdown = resourceManager.shutdown() && allShutdown;
        }

        allShutdown = RMIMiddleware.shutdown() && allShutdown;

        return allShutdown;
    }
}
