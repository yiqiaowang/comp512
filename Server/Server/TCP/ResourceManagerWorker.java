package Server.TCP;

import Server.TCP.TCPResourceManager;
import Server.Common.Procedure;
import Server.Common.ProcedureRequest;
import Server.Common.ProcedureResponse;

import java.net.*;
import java.io.*;
import java.lang.*;

public class ResourceManagerWorker implements Runnable {
    
    private TCPResourceManager manager;
    private ProcedureRequest request;

    public ResourceManagerWorker (ProcedureRequest request, TCPResourceManager manager) {
        this.request = request;
        this.manager = manager;
    }

    public void run() {
        try {
            System.out.println("Launched thread to serve client on resource manager.");
            ProcedureResponse response = this.manager.executeRequest(this.request);
            this.manager.sendResponse(response);
            System.out.println("Successfuly served client request");
        } catch(Exception e){
            System.out.println("Something bad happened in a ResourceManager worker thread!!!");
            e.printStackTrace();
        }
    }
}
