package Server.RMI.middleware;

import Server.Interface.IResourceManager;
import Server.RMI.RMIMiddleware;
import org.junit.BeforeClass;
import org.junit.Test;

import java.rmi.RemoteException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MiddlewareTest {
    private static IResourceManager resourceManager = RMIMiddleware.initializeMiddleware(1100, new String[] {"localhost:1090", "localhost:1091", "localhost:1092", "localhost:1093"});

    @BeforeClass
    public static void setup() throws RemoteException {
        resourceManager.newCustomer(1, 1234);
    }


    @Test
    public void flight() throws Exception {
        boolean flightAdded = resourceManager.addFlight(1, 1, 10, 500);
        assertTrue(flightAdded);
        int numSeats = resourceManager.queryFlight(1, 1);
        assertEquals(10, numSeats);

        int price = resourceManager.queryFlightPrice(3, 1);
        assertEquals(500, price);

        // update the number of seats
        resourceManager.addFlight(1,1,20,-1);
        numSeats = resourceManager.queryFlight(1,1);
        assertEquals(30, numSeats);


        boolean flightReserved = resourceManager.reserveFlight(2, 1234, 1);
        assertTrue(flightReserved);

        boolean flightRemoved = resourceManager.deleteFlight(3, 1);
        assertFalse(flightRemoved);        // since someone has booked it

        flightReserved = resourceManager.addFlight(1, 2, 10, 1000);
        assertTrue(flightReserved);
        numSeats = resourceManager.queryFlight(1, 2);
        assertEquals(10, numSeats);

        flightRemoved = resourceManager.deleteFlight(3, 2);
        assertTrue(flightRemoved);
    }

    @Test
    public void cars() throws Exception {
        boolean carsAdded = resourceManager.addCars(3, "Rome", 12, 200);
        assertTrue(carsAdded);

        int numCars = resourceManager.queryCars(3, "Rome");
        assertEquals(12, numCars );

        int price = resourceManager.queryCarsPrice(3, "Rome");
        assertEquals(200, price);

        // add cars
        resourceManager.addCars(3, "Rome", 8, 200);

        numCars = resourceManager.queryCars(3, "Rome");
        assertEquals(20, numCars);

        boolean carReserved = resourceManager.reserveCar(4, 1234, "Rome");
        assertTrue(carReserved);

        boolean carRemoved = resourceManager.deleteCars(3, "Rome");
        assertFalse(carRemoved);        // since someone has booked it

        resourceManager.addCars(1, "Montreal", 2, 1000);
        numCars = resourceManager.queryCars(1, "Montreal");
        assertEquals(2, numCars);

        carRemoved = resourceManager.deleteCars(3, "Montreal");
        assertTrue(carRemoved);
    }

    @Test
    public void rooms() throws Exception {
        boolean roomsAdded = resourceManager.addRooms(3, "Rome", 12, 200);
        assertTrue(roomsAdded);

        int numRooms = resourceManager.queryRooms(3, "Rome");
        assertEquals(12, numRooms);

        int price = resourceManager.queryRoomsPrice(3, "Rome");
        assertEquals(200, price);

        // add rooms
        resourceManager.addRooms(3, "Rome", 8, 200);

        numRooms = resourceManager.queryRooms(3, "Rome");
        assertEquals(20, numRooms);

        boolean carReserved = resourceManager.reserveRoom(4, 1234, "Rome");
        assertTrue(carReserved);

        boolean roomRemoved = resourceManager.deleteRooms(4, "Rome");
        assertFalse(roomRemoved);        // since someone has booked it

        resourceManager.addRooms(1, "Montreal", 2, 1000);
        numRooms = resourceManager.queryRooms(1, "Montreal");
        assertEquals(2, numRooms);

        roomRemoved = resourceManager.deleteRooms(3, "Montreal");
        assertTrue(roomRemoved);
    }

    @Test
    public void customer() throws RemoteException {
        String customerInfo = resourceManager.queryCustomerInfo(1, 100);
        assertEquals("", customerInfo);

        customerInfo = resourceManager.queryCustomerInfo(1, 1234);
        assertTrue(customerInfo.length() > 0);

        boolean created = resourceManager.newCustomer(1, 1234);
        assertFalse(created);

        created = resourceManager.newCustomer(1, 100);
        assertTrue(created);

        boolean deleted = resourceManager.deleteCustomer(1, 100);
        assertTrue(deleted);
    }
}