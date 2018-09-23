package Server.Common;

import java.io.Serializable;

public class ProcedureResponse implements Serializable {
    private static final long serialVersionUID = 1;
    private String stringResponse = "";
    private int intResponse = 0;
    private boolean booleanResponse = false;
    private Procedure procedure;

    public ProcedureResponse() { }

    public ProcedureResponse(Procedure procedure) {
        this.procedure = procedure;
    }

    public void setBooleanResponse(boolean response) {
        this.booleanResponse = response;
    }

    public void setStringResponse(String response) {
        this.stringResponse = response;
    }

    public void setIntResponse(int response) {
        this.intResponse = response;
    }

    public Procedure getProcedure() {
        return this.procedure;
    }

    public boolean getBooleanResponse() {
        return this.booleanResponse;
    }

    public String getStringResponse() {
        return this.stringResponse;
    }

    public int getIntResponse() {
        return this.intResponse;
    }
}
