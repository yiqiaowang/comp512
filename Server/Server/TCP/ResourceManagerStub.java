package Server.TCP;

import Server.Common.ProcedureRequest;
import Server.Common.ProcedureResponse;

import java.net.*;
import java.io.*;

public class ResourceManagerStub {
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ResourceManagerStub(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    public ProcedureResponse executeRemoteProcedure(ProcedureRequest request) throws IOException, ClassNotFoundException {
        this.out.writeObject(request);
        return (ProcedureResponse) this.in.readObject();
    }
}
