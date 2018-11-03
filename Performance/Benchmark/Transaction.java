package Benchmark;

import java.util.Arrays;

// Import client here

public class Transaction {
    
    private myClient client; 

    // Store array of reservable flights
    // Store array of reservable locations
    private Array<int> flights;
    private Array<String> locations;

    public int getFlightNumber() {
        return this.flights[(int)(Math.random() * this.flights.length)];    
    }

    public String getLocation() {
        return this.locations[(int)(Math.random() * this.locations.length)];    
    }

    public Transaction(flights, locations) {
        this.flights = flights;
        this.locations = locations;
    }

    public void abstract run();
}
