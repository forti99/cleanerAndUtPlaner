package entities;

import java.awt.geom.Point2D;

public class Village {
    private final String ownerName;
    private final Point2D location;
    private int[] units;
    private int cleanerToSendAmount;

    public Village(String ownerName, double x, double y, int[] units, int cleanerToSendAmount) {
        this.ownerName = ownerName;
        this.location = new Point2D.Double(x, y);
        this.units = units;
        this.cleanerToSendAmount = cleanerToSendAmount;
    }

    //Getter + Setter
    public String getOwnerName() {
        return ownerName;
    }

    public Point2D getLocation() {
        return location;
    }

    public int[] getUnits() {
        return units;
    }

    public int getCleanerToSendAmount() {
        return cleanerToSendAmount;
    }

    public void setCleanerToSendAmount(int cleanerToSendAmount) {
        this.cleanerToSendAmount = cleanerToSendAmount;
    }
}