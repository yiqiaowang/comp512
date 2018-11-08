package Benchmark;

import Client.RMIClient;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

public class MultiResourceTransaction extends Transaction {
    
    public Vector<String> reserveFlightArgs(int xid, int customer_id, int flight_num) {
        Vector<String> args = new Vector<>(4);
        args.add("flight");
        args.add(String.valueOf(xid));
        args.add(String.valueOf(customer_id));
        args.add(String.valueOf(flight_num));
        return args;
    }

    public Vector<String> reserveLocationArgs(int xid, int customer_id, String location) {
        Vector<String> args = new Vector<>(4);
        args.add("location");
        args.add(String.valueOf(xid));
        args.add(String.valueOf(customer_id));
        args.add(location);
        return args;
    } 

    // Reserve 3 flights for now, client.execute is a blocking call
    public void run() throws InterruptedException, RemoteException, NumberFormatException {
        for (int i = 0; i < 3; i++) {
            System.out.println("Reserving flight with " +
                this.reserveFlightArgs(
                    this.identifier,
                    this.identifier,
                    this.getFlightNumber())
                );
            Thread.sleep(125);
        }
        // this.client.execute(Command.ReserveFlight,
        //         this.reserveFlightArgs(
        //             this.identifier,
        //             this.identifier,
        //             this.getFlightNumber())
        //         );

        // this.client.execute(Command.ReserveRoom,
        //         this.reserveLocationArgs(
        //             this.identifier,
        //             this.identifier,
        //             this.getRoomLocation())
        //         );

        // this.client.execute(Command.ReserveCar,
        //         this.reserveLocationArgs(
        //             this.identifier,
        //             this.identifier,
        //             this.getCarLocation())
        //         );
    }

    public MultiResourceTransaction(
            int identifier,
            RMIClient client,
            ArrayList<Integer> flight_numbers,
            ArrayList<String> hotel_locations,
            ArrayList<String> car_locations) {
        super(identifier, client, flight_numbers, hotel_locations, car_locations);
    } 
}
