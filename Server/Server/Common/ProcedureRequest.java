package Server.Common;

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
    private int id = 0;

    // Method call parameters
    private int resourceID;
    private int resourceAmount;
    private int resourcePrice;
    private String location;
    private int reserveID;

    // Use in the bundle procedure
    private Vector<String> resourceIDs;
    private boolean requireCar;
    private boolean requireRoom;

        
    public ProcedureRequest(Procedure procedure) {
        this.procedure = procedure; 
    }

    public int getReserveID() {
        return this.reserveID;
    }

    public int getXID() {
        return this.id;
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

    public void setXID(int id) {
        this.id = id;
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

    public void setRequireRoom(boolean require) {
        this.requireRoom = require;
    }

    public void setReserveID(int id) {
        this.reserveID = id;
    }

}
