package Server.RMI.middleware;

import Server.Common.Car;
import Server.Common.Flight;
import Server.Common.Room;
import Server.Common.PeerStatus;
import Server.Interface.IResourceManager;
import Server.Interface.InvalidTransactionException;
import Server.RMI.RMIMiddleware;
import Server.Transaction.ResourceLockRequest;
import Server.Transaction.TransactionManager;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.util.*;

import static Server.Common.Services.*;
import static Server.LockManager.TransactionLockObject.LockType.LOCK_READ;
import static Server.LockManager.TransactionLockObject.LockType.LOCK_WRITE;

public class Middleware implements IResourceManager {
    private static final String name = "Middleware";

    // Failure Detection of resource managers
    private HashMap<IResourceManager, PeerStatus> resourceManagerStatus = new HashMap<>();
    private ArrayList<IResourceManager> failedPeers = new ArrayList<>();

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

        this.resourceManagerStatus.put(this.flightsResourceManager,
                new PeerStatus());
        this.resourceManagerStatus.put(this.roomsResourceManager,
                new PeerStatus());
        this.resourceManagerStatus.put(this.carsResourceManager,
                new PeerStatus());
        this.resourceManagerStatus.put(this.customersResourceManager,
                new PeerStatus());

        
        // Start health checks here
        Thread checkForFailures = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }

                for (IResourceManager peer : this.resourceManagerStatus.keySet()) {
                    try {
                        PeerStatus status = this.resourceManagerStatus.get(peer);
                        String peerName = peer.getName();
                        if (System.currentTimeMillis() > status.getTTL()) {
                            if (peer.isAlive()) {
                                status.setTTL(System.currentTimeMillis() + 2000);
                            } else {
                                this.failedPeers.add(peer);
                                // Do something when once failure is detected. 
                                System.out.println("Suspected timeout failure at " +
                                        peerName);
                            }
                        }
                    } catch(RemoteException e){
                        this.failedPeers.add(peer);
                        System.out.println("Remote failure exception caught during health checks");
                    }
                }
                System.out.println("Health checks passed!");
            }
        });
        checkForFailures.start();
    }

    public void notifyResourceManagers(String host, int port) {
        // Tell resource managers about middleware
        try {
            this.flightsResourceManager.startHealthChecks(host, port);
            this.roomsResourceManager.startHealthChecks(host, port);
            this.carsResourceManager.startHealthChecks(host, port);
            this.customersResourceManager.startHealthChecks(host, port);
        } catch(NotBoundException | RemoteException e){
            System.out.println("Remote failure exception caught during health checks");
            e.printStackTrace();
        }
    }

    @Override
    public void startHealthChecks(String host, int port) {
        System.out.println("startHealthChecks should not be called at the middleware");
        return; 
    }


    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(FLIGHTS.toString(), flightNum, flightsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CARS.toString(), location, carsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(ROOMS.toString(), location, roomsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        try {
            int customerId = customersResourceManager.newCustomer(id);
            boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CUSTOMERS.toString(), customerId, customersResourceManager, LOCK_WRITE));
            if (!locksAcquired) {
                transactionManager.abort(id);
                throw new InvalidTransactionException(id);
            } else {
                return customerId;
            }
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CUSTOMERS.toString(), cid, customersResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(FLIGHTS.toString(), flightNum, flightsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CARS.toString(), location, carsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(ROOMS.toString(), location, roomsResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CUSTOMERS.toString(), customerID, customersResourceManager, LOCK_WRITE));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(FLIGHTS.toString(), flightNumber, flightsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CARS.toString(), location, carsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(ROOMS.toString(), location, roomsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CUSTOMERS.toString(), customerID, customersResourceManager, LOCK_READ));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(FLIGHTS.toString(), flightNumber, flightsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(CARS.toString(), location, carsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
        boolean locksAcquired = transactionManager.requestLocksOnResources(id, new ResourceLockRequest(ROOMS.toString(), location, roomsResourceManager, LOCK_READ));
        if (!locksAcquired) {
            transactionManager.abort(id);
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
                new ResourceLockRequest(CUSTOMERS.toString(), customerID, customersResourceManager, LOCK_WRITE),
                new ResourceLockRequest(FLIGHTS.toString(), flightNumber, flightsResourceManager, LOCK_WRITE));

        if (!locksAcquired) {
            transactionManager.abort(id);
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
            customersResourceManager.reserveCustomer(id, customerID, Flight.getKey(flightNumber), String.valueOf(flightNumber), flightPrice);

            return true;
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean reserveCar(int id, int customerID, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id,
                new ResourceLockRequest(CUSTOMERS.toString(), customerID, customersResourceManager, LOCK_WRITE),
                new ResourceLockRequest(CARS.toString(), location, carsResourceManager, LOCK_WRITE));

        if (!locksAcquired) {
            transactionManager.abort(id);
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

            customersResourceManager.reserveCustomer(id, customerID, Car.getKey(location), location, rentalPrice);

            return true;
        } catch (RemoteException | InvalidTransactionException e) {
            transactionManager.abort(id);
            throw e;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customerID, String location) throws RemoteException, InvalidTransactionException {
        boolean locksAcquired = transactionManager.requestLocksOnResources(id,
                new ResourceLockRequest(CUSTOMERS.toString(), customerID, customersResourceManager, LOCK_WRITE),
                new ResourceLockRequest(ROOMS.toString(), location, roomsResourceManager, LOCK_WRITE));

        if (!locksAcquired) {
            transactionManager.abort(id);
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
            customersResourceManager.reserveCustomer(id, customerID, Room.getKey(location), location, roomPrice);

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

        List<ResourceLockRequest> requiredResources = new ArrayList<>();
        requiredResources.add(new ResourceLockRequest(CUSTOMERS.toString(), customerID, customersResourceManager, LOCK_WRITE));

        for (String flightNumber: flightNumbers) {
            requiredResources.add(new ResourceLockRequest(FLIGHTS.toString(), flightNumber, flightsResourceManager, LOCK_WRITE));
        }

        if (car) {
            requiredResources.add(new ResourceLockRequest(CARS.toString(), location, carsResourceManager, LOCK_WRITE));
        }

        if (room) {
            requiredResources.add(new ResourceLockRequest(ROOMS.toString(), location, roomsResourceManager, LOCK_WRITE));
        }


        boolean locksAcquired = transactionManager.requestLocksOnResources(id, requiredResources);

        if (!locksAcquired) {
            transactionManager.abort(id);
            throw new InvalidTransactionException(id);
        }

        Map<String, Integer> flightCounts = new HashMap<>();


        try {
            for (String flightNumber : flightNumbers) {
                int numFlightsAvailable = flightsResourceManager.queryFlight(id, Integer.valueOf(flightNumber));
                int flightCountForBundle = flightCounts.getOrDefault(flightNumber, 0) + 1;

                if (numFlightsAvailable < flightCountForBundle) {
                    return false;
                }

                flightCounts.put(flightNumber, flightCountForBundle);
            }

            if (car) {
                int numCarsAvailable = carsResourceManager.queryCars(id, location);
                if (numCarsAvailable < 1) {
                    return false;
                }

            }
            if (room) {
                int numRoomsAvailable = roomsResourceManager.queryRooms(id, location);
                if (numRoomsAvailable < 1) {
                    return false;
                }
            }

            for (String flightNumber: flightNumbers) {
                flightsResourceManager.reserveFlight(id, customerID, Integer.parseInt(flightNumber));
            }

            if (car) {
                carsResourceManager.reserveCar(id, customerID, location);
            }

            if (room) {
                roomsResourceManager.reserveRoom(id, customerID, location);
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

    public boolean isAlive() throws RemoteException { 
        return true;
    }
}
