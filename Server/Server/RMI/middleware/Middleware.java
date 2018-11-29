package Server.RMI.middleware;

import Server.Common.Car;
import Server.Common.Flight;
import Server.Common.Room;
import Server.Common.PeerStatus;
import Server.Common.CrashModes;
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
    private final TransactionManager transactionManager = TransactionManager.initialize();

    // TODO: Store set of committed transactions
    private final Set<Integer> committedTransactions = new HashSet<>();


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
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    return;
                }

                System.out.println("Running health checks!");
                for (IResourceManager peer : this.resourceManagerStatus.keySet()) {
                    try {
                        PeerStatus status = this.resourceManagerStatus.get(peer);
                        String peerName = peer.getName();
                        if (System.currentTimeMillis() > status.getTTL()) {
                            // If the `isAlive' method call fails
                            // we go directly to the catch block
                            peer.isAlive();
                            status.setTTL(System.currentTimeMillis() + 2000);
                        }
                    } catch (RemoteException e) {
                        this.failedPeers.add(peer);
                        System.out.println("Suspected failure detected at a resource manager!");
                    }
                }
            }
        });
        checkForFailures.start();
    }

    public void notifyResourceManagers(String host, int port) {
        // Tell resource managers about middleware
        try {
            this.flightsResourceManager.startFailureDetector(host, port);
            this.roomsResourceManager.startFailureDetector(host, port);
            this.carsResourceManager.startFailureDetector(host, port);
            this.customersResourceManager.startFailureDetector(host, port);
        } catch(NotBoundException | RemoteException e){
            System.out.println("Remote failure exception caught during health checks");
            e.printStackTrace();
        }
    }

    @Override
    public void startFailureDetector(String host, int port) {
        System.out.println("startFailureDetector should not be called at the middleware");
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
        System.out.println("Commit " + xid + " called");
        try {
            if (prepare(xid)) {
                System.out.println("prepare = true");
                synchronized (committedTransactions) {
                    committedTransactions.add(xid);
                }
                transactionManager.commit(xid);
                return true;
            }
        } catch (InvalidTransactionException ignored) { /* Never gets caught - the exception is caught in prepare() */ }

        System.out.println("prepare = false");

        transactionManager.abort(xid);
        return false;
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
    @Override
    public void resetCrashes() throws RemoteException {
        this.transactionManager.chaosMonkey.disableAll();
    }

    @Override
    public void crashMiddleware(int mode) throws RemoteException {
        CrashModes cmode = CrashModes.INVALID;
        switch (mode) {
            case 1: cmode = CrashModes.T_ONE;
                    break;
            case 2: cmode = CrashModes.T_TWO;
                    break;
            case 3: cmode = CrashModes.T_THREE;
                    break;
            case 4: cmode = CrashModes.T_FOUR;
                    break;
            case 5: cmode = CrashModes.T_FIVE;
                    break;
            case 6: cmode = CrashModes.T_SIX;
                    break;
            case 7: cmode = CrashModes.T_SEVEN;
                    break;
            case 8: cmode = CrashModes.T_EIGHT;
                    break;
        }
        this.transactionManager.chaosMonkey.enableCrashMode(cmode);
    }

    @Override
    public void crashResourceManager(String name, int mode) throws RemoteException {
        if (name.equals(flightsResourceManager.getName().toLowerCase())) {
            flightsResourceManager.crashResourceManager(name, mode);
        }
        else if (name.equals(carsResourceManager.getName().toLowerCase())){
                carsResourceManager.crashResourceManager(name, mode);
        }
        else if (name.equals(roomsResourceManager.getName().toLowerCase())) {
                roomsResourceManager.crashResourceManager(name, mode);
        }
        else if (name.equals(customersResourceManager.getName().toLowerCase())) {
                customersResourceManager.crashResourceManager(name, mode);
        } else {
            System.out.println("Could not find resource manager: " + name);
            System.out.println("Available names are");
            System.out.println(flightsResourceManager.getName());
            System.out.println(carsResourceManager.getName());
            System.out.println(roomsResourceManager.getName());
            System.out.println(customersResourceManager.getName());
        }
    }


    public boolean[] transactionsCommitted(int[] xids) throws RemoteException {
        boolean[] committed = new boolean[xids.length];
        synchronized (committedTransactions) {
            for (int i = 0; i < xids.length; i++) {
                committed[i] = committedTransactions.contains(xids[i]);
            }
        }

        return committed;
    }


    @Override
    public boolean prepare(int xid) throws RemoteException, InvalidTransactionException {  
        int numTimeouts = 5;
        for (int i = 0; i < numTimeouts; i++) {
            try {
                return transactionManager.prepare(xid);
            } catch (RemoteException | InvalidTransactionException e) {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e1) {
                    return false;
                }
            }
        }

        return false;
     }

    @Override
    public boolean prepare_crash(int xid, long timeout){
        System.out.println("Prepare crash should not be called at the middleware!");  
        return false;
    }
}
