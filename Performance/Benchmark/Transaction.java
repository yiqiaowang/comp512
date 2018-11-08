package Benchmark;

import Client.Command;
import Client.RMIClient;
import Server.Interface.InvalidTransactionException;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

public abstract class Transaction {
    
    public RMIClient client; 
    public int identifier;
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

    public void setupCustomer() throws RemoteException, NumberFormatException, InvalidTransactionException {
        Vector<String> args = new Vector<>(3);
        args.add("setup customerid");
        args.add(String.valueOf(this.identifier));
        args.add(String.valueOf(this.identifier));
        
        this.client.execute(Command.AddCustomerID, args);
    }

    public Transaction(
            int identifier,
            RMIClient client,
            ArrayList<Integer> flight_numbers,
            ArrayList<String> room_locations,
            ArrayList<String> car_locations) {
        this.identifier = identifier;
        this.client = client;
        this.flight_numbers = flight_numbers;
        this.room_locations = room_locations;
        this.car_locations = car_locations;
    }

    public void connectServer() throws ConnectException {
        this.client.connectServer();
    }

    public abstract void run() throws InterruptedException, RemoteException, NumberFormatException, InvalidTransactionException;
}
