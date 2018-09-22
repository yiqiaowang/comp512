package Server.Common;

import Server.Common.Procedure;

import java.io.Serializable;
import java.util.Vector;


public class ProcedureRequest implements Serializable {
    private static final long serialVersionUID = 1;

    /*
     * Private members, function parameters, and request id.
     */

    // The procedure being called
    private Procedure procedure; 

    // Request ID, see <xid>
    private int id;

    // Method call parameters
    private int resourceID;
    private int resourceAmount;
    private int resourcePrice;
    private String location;

    // Use in the bundle procedure
    private Vector<String> resourceIDs;
    private boolean requireCar;
    private boolean requireRoom;

        
    public ProcedureRequest(Procedure procedure) {
        this.procedure = procedure; 
    }

    public Procedure getProcedure() {
        return this.procedure;
    }

    public int getResourceID() {
        return this.resourceID;
    }

    public int getResourceAmount() {
        return this.resourceAmount; 
    }

    public int getResourcePrice() {
        return this.resourcePrice;
    }

    public String getLocation() {
        return this.location;
    }

    public Vector<String> getResourceIDs() {
        return this.resourceIDs;
    }

    public boolean getRequireCar() {
        return this.requireCar;
    }

    public boolean getRequireRoom() {
        return this.requireRoom;
    }

    public void setResourceID(int id) {
        this.resourceID = id;
    }

    public void setResourceAmount(int amount) {
        this.resourceAmount = amount;
    }

    public void setResourcePrice(int price) {
        this.resourcePrice = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setResourceIDs(Vector<String> ids) {
        this.resourceIDs = ids;
    }

    public void setRequireCar(boolean require) {
        this.requireCar = require;
    }

    public void requiresRoom(boolean require) {
        this.requireRoom = require;
    }

}
