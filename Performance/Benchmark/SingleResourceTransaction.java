package Benchmark;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.Vector;
import java.util.ArrayList;

import Client.Command;
import Client.RMIClient;

public class SingleResourceTransaction extends Transaction {

    public Vector<String> reserveFlightArgs(int xid, int customer_id, int flight_num) {
        Vector<String> args = new Vector(4);
        args.add("reserveFlight");
        args.add(String.valueOf(xid));
        args.add(String.valueOf(customer_id));
        args.add(String.valueOf(flight_num));
        return args;
    }

    // Reserve 3 flights for now, client.execute is a blocking call
    public void run() throws RemoteException, NumberFormatException {
        this.client.execute(Command.ReserveFlight,
                this.reserveFlightArgs(
                    1, // Hardcoded transaction ID for now
                    this.customer_id,
                    this.getFlightNumber())
                );

        this.client.execute(Command.ReserveFlight,
                this.reserveFlightArgs(
                    1, // Hardcoded transaction ID for now
                    this.customer_id,
                    this.getFlightNumber())
                );

        this.client.execute(Command.ReserveFlight,
                this.reserveFlightArgs(
                    1, // Hardcoded transaction ID for now
                    this.customer_id,
                    this.getFlightNumber())
                );
    }

    public SingleResourceTransaction(
            RMIClient client,
            ArrayList<Integer> flight_numbers,
            ArrayList<String> hotel_locations,
            ArrayList<String> car_locations) {
        super(client, flight_numbers, hotel_locations, car_locations);
    } 
}
