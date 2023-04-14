package entities;

import java.awt.*;

public class Village {
    private final String ownerName;
    private final Point location;
    private final int[] units;
    private int cleanerOrUtToSendAmount;

    public Village(String ownerName, Point location, int[] units, int cleanerOrUtToSendAmount) {
        this.ownerName = ownerName;
        this.location = location;
        this.units = units;
        this.cleanerOrUtToSendAmount = cleanerOrUtToSendAmount;
    }

    //Getter + Setter
    public String getOwnerName() {
        return ownerName;
    }

    public Point getLocation() {
        return location;
    }

    public int[] getUnits() {
        return units;
    }

    public int getCleanerOrUtToSendAmount() {
        return cleanerOrUtToSendAmount;
    }

    public void addCleanerOrUtToSendAmount(int amountToAdd) {
        this.cleanerOrUtToSendAmount += amountToAdd;
    }
}