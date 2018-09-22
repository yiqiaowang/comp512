package Client;

import Server.Common.Procedure;
import Server.Common.ProcedureRequest;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPCommunicationManager {
    
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public TCPCommunicationManager(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public boolean executeProcedure(ProcedureRequest procedure) {
        out.writeObject(procedure);
        // TODO: Check for response here!!!! 
        //
        // // BLOCKING PROCEDURE CALL
        // Boolean ack = in.readObject();
        // if (ack.somethign...) {
        //     return true;
        // } else {
        //     return false;
        // }

        return false;
    }
}
