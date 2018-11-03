package Benchmark;

import Client.Command;
import Client.RMIClient;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

public abstract class Transaction {
    
    public RMIClient client; 
    public int customer_id;
    public ArrayList<Integer> flight_numbers;
    public ArrayList<String> room_locations;
    public ArrayList<String> car_locations;

    public int getFlightNumber() {
        return this.flight_numbers.get(
            (int)(Math.random() * this.flight_numbers.size())
        );    
    }

    public String getCarLocation() {
        return this.car_locations.get(
            (int)(Math.random() * this.car_locations.size())
        );    
    }

    public String getRoomLocation() {
        return this.room_locations.get(
            (int)(Math.random() * this.room_locations.size())
        );    
    }

    public void setupCustomer(int id) throws RemoteException, NumberFormatException {
        Vector<String> args = new Vector(3);
        args.add("setup customerid");
        args.add("1"); // hard coded xid
        args.add(String.valueOf(id));
        
        // TODO PUT BACK WHEN YOU FIGURE OUT HOW TO SETUP RMI SERVER
        // this.client.execute(Command.AddCustomerID, args);
        this.customer_id = id;
    }

    public Transaction(
            RMIClient client,
            ArrayList<Integer> flight_numbers,
            ArrayList<String> room_locations,
            ArrayList<String> car_locations) {
        this.client = client;
        this.flight_numbers = flight_numbers;
        this.room_locations = room_locations;
        this.car_locations = car_locations;
    }

    public void connectServer() throws ConnectException {
        this.client.connectServer();
    }

    public abstract void run() throws InterruptedException, RemoteException, NumberFormatException;
}
