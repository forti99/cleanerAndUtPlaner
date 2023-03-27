package entities;

import java.awt.geom.Point2D;

public class Village {
    private final String ownerName;
    private final Point2D location;
    private int[] units;

    public Village(String ownerName, double x, double y, int[] units) {
        this.ownerName = ownerName;
        this.location = new Point2D.Double(x, y);
        this.units = units;
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
}