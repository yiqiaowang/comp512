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
    
    /* Meta functions */
    Error,
    RegisterResourceManager,
    RegisterAcknowledge,

    // Following are used to implement Bundle
    DecrementFlightsAvailable,
    IncrementFlightsAvailable,
    DecrementCarsAvailable,
    IncrementCarsAvailable,
    DecrementRoomsAvailable,
    IncrementRoomsAvailable,
    AddCarReservation,
    AddRoomReservation,
    AddFlightReservation
}
