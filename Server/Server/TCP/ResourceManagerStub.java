package Server.TCP;

import Server.Common.ProcedureRequest;
import Server.Common.ProcedureResponse;

import java.net.*;
import java.io.*;

public class ResourceManagerStub {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;


    public ResourceManagerStub(String server, int port) throws IOException, UnknownHostException {
        this.socket = new Socket(server, port);
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
    }

    public ProcedureResponse executeProcedure(ProcedureRequest request) throws IOException, ClassNotFoundException {
        this.out.writeObject(request);
        return (ProcedureResponse) this.in.readObject();
    }
}
