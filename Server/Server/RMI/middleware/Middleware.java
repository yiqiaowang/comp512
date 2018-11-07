package Server.RMI.middleware;

import Server.Common.Car;
import Server.Common.Flight;
import Server.Common.Room;
import Server.Interface.IResourceManager;
import Server.LockManager.TransactionLockObject.LockType;
import Server.Transaction.InvalidTransactionException;
import Server.Transaction.ResourceLockRequest;
import Server.Transaction.TransactionAbortedException;
import Server.Transaction.TransactionManager;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static Server.Common.Services.*;

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

    private <T> T addTransactionOperation(int transactionId,
                                        IResourceManager resourceManager,
                                        String resourceName,
                                        LockType lockType,
                                        SupplierWithRemoteException<T> operation) throws RemoteException, InvalidTransactionException, TransactionAbortedException
    {
        try
        {
            boolean lockAcquired = transactionManager.requestLockOnResource(transactionId, resourceManager, resourceName, lockType);
            if (!lockAcquired) {
                throw new InvalidTransactionException(transactionId);
            }

            return operation.operation();
        }
        catch (RemoteException | InvalidTransactionException | TransactionAbortedException e)
        {
            transactionManager.abort(transactionId);
            throw e;
        }
    }




    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Boolean> operation = () -> flightsResourceManager.addFlight(id, flightNum, flightSeats, flightPrice);
        List<ResourceLockRequest> resourceLockRequests = Collections.singletonList(
                new ResourceLockRequest(FLIGHTS.toString(), flightsResourceManager, LockType.LOCK_WRITE)
        );

        transactionManager.addOperation(id, resourceLockRequests, operation);


        return addTransactionOperation(id, flightsResourceManager, FLIGHTS.toString(), LockType.LOCK_WRITE, operation);

    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Boolean> operation = () -> carsResourceManager.addCars(id, location, numCars, price);
        return addTransactionOperation(id, carsResourceManager, CARS.toString(), LockType.LOCK_WRITE, operation);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Boolean> operation = () -> roomsResourceManager.addRooms(id, location, numRooms, price);
        return addTransactionOperation(id, roomsResourceManager, ROOMS.toString(), LockType.LOCK_WRITE, operation);
    }

    @Override
    public int newCustomer(int id) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Integer> operation = () -> customersResourceManager.newCustomer(id);
        return addTransactionOperation(id, customersResourceManager, CUSTOMERS.toString(), LockType.LOCK_WRITE, operation);

    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Boolean> operation = () -> customersResourceManager.newCustomer(id, cid);
        return addTransactionOperation(id, customersResourceManager, CUSTOMERS.toString(), LockType.LOCK_WRITE, operation);
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Boolean> operation = () -> flightsResourceManager.deleteFlight(id, flightNum);
        return addTransactionOperation(id, flightsResourceManager, FLIGHTS.toString(), LockType.LOCK_WRITE, operation);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Boolean> operation = () -> carsResourceManager.deleteCars(id, location);
        return addTransactionOperation(id, carsResourceManager, CARS.toString(), LockType.LOCK_WRITE, operation);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Boolean> operation = () -> roomsResourceManager.deleteRooms(id, location);
        return addTransactionOperation(id, roomsResourceManager, ROOMS.toString(), LockType.LOCK_WRITE, operation);
    }

    @Override
    public boolean deleteCustomer(int id, int customerID) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Boolean> operation = () -> customersResourceManager.deleteCustomer(id, customerID);
        return addTransactionOperation(id, customersResourceManager, CUSTOMERS.toString(), LockType.LOCK_WRITE, operation);
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Integer> operation = () -> flightsResourceManager.queryFlight(id, flightNumber);
        return addTransactionOperation(id, flightsResourceManager, FLIGHTS.toString(), LockType.LOCK_READ, operation);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Integer> operation = () -> carsResourceManager.queryCars(id, location);
        return addTransactionOperation(id, carsResourceManager, CARS.toString(), LockType.LOCK_READ, operation);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Integer> operation = () -> roomsResourceManager.queryRooms(id, location);
        return addTransactionOperation(id, roomsResourceManager, ROOMS.toString(), LockType.LOCK_READ, operation);
    }

    @Override
    public String queryCustomerInfo(int id, int customerID) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<String> operation = () -> customersResourceManager.queryCustomerInfo(id, customerID);
        return addTransactionOperation(id, customersResourceManager, CUSTOMERS.toString(), LockType.LOCK_READ, operation);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        SupplierWithRemoteException<Integer> operation = () -> flightsResourceManager.queryFlightPrice(id, flightNumber);
        return addTransactionOperation(id, flightsResourceManager, FLIGHTS.toString(), LockType.LOCK_READ, operation);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException, InvalidTransactionException, TransactionAbortedException {
        SupplierWithRemoteException<Integer> operation = () -> carsResourceManager.queryCarsPrice(id, location);
        return addTransactionOperation(id, carsResourceManager, CARS.toString(), LockType.LOCK_READ, operation);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException, InvalidTransactionException, TransactionAbortedException {
        SupplierWithRemoteException<Integer> operation = () -> roomsResourceManager.queryRoomsPrice(id, location);
        return addTransactionOperation(id, roomsResourceManager, ROOMS.toString(), LockType.LOCK_READ, operation);
    }

    @Override
    public boolean reserveFlight(int id, int customerID, int flightNumber) throws RemoteException, InvalidTransactionException, TransactionAbortedException {
        try
        {
            boolean lockAcquired = transactionManager.requestLockOnResource(id, customersResourceManager, CUSTOMERS.toString(), LockType.LOCK_WRITE);
            if (!lockAcquired) {
                throw new InvalidTransactionException(id);
            }
            lockAcquired = transactionManager.requestLockOnResource(id, flightsResourceManager, FLIGHTS.toString(), LockType.LOCK_WRITE);
            if (!lockAcquired) {
                throw new InvalidTransactionException(id);
            }

            String customerInfo = customersResourceManager.queryCustomerInfo(id, customerID);

            if (customerInfo == null || customerInfo.trim().equals("")) {
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
        catch (RemoteException | InvalidTransactionException | TransactionAbortedException e)
        {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean reserveCar(int id, int customerID, String location) throws RemoteException, InvalidTransactionException, TransactionAbortedException
    {
        try
        {
            boolean lockAcquired = transactionManager.requestLockOnResource(id, customersResourceManager, CUSTOMERS.toString(), LockType.LOCK_WRITE);
            if (!lockAcquired) {
                throw new InvalidTransactionException(id);
            }
            lockAcquired = transactionManager.requestLockOnResource(id, carsResourceManager, CARS.toString(), LockType.LOCK_WRITE);
            if (!lockAcquired) {
                throw new InvalidTransactionException(id);
            }
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
        catch (RemoteException | InvalidTransactionException | TransactionAbortedException e)
        {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customerID, String location) throws RemoteException, InvalidTransactionException, TransactionAbortedException {
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
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException, InvalidTransactionException, TransactionAbortedException {
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

    public int start() {
        return transactionManager.startTransaction();
    }

    public boolean commit(int transactionId) throws InvalidTransactionException {
        transactionManager.commit(transactionId);
        return true;
    }

    public void abort(int transactionId) {
        transactionManager.abort(transactionId);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
