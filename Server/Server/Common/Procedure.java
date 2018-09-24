package Server.Common;

import java.io.Serializable;

public enum Procedure implements Serializable {
    AddFlight,
    AddCars,
    AddRooms,
    AddCustomer,
    AddCustomerID,
    DeleteFlight,
    DeleteCars,
    DeleteRooms,
    DeleteCustomer,
    QueryFlight,
    QueryCars,
    QueryRooms,
    QueryCustomer,
    QueryFlightPrice,
    QueryCarsPrice,
    QueryRoomsPrice,
    ReserveFlight,
    ReserveCar,
    ReserveRoom,
    Bundle,
    
    // Meta functions
    Error,
    RegisterResourceManager,
    RegisterAcknowledge,
    RemoveResourceManager
}
