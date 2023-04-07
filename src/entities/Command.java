package entities;

import processing.Calculator;
import processing.DataProcessor;
import util.Settings;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Command implements Comparable<Command> {
    private Point2D target;
    private Point2D origin;
    private LocalDateTime arrival;
    private Unit unit;
    private String senderName;
    private Runtime runtime;

    public Command(Point2D target, Point2D origin, LocalDateTime arrival, Unit unit, String senderName) {
        this.target = target;
        this.origin = origin;
        this.arrival = arrival;
        this.unit = unit;
        this.senderName = senderName;
        runtime = calculateRuntime();
    }

    public String toUltimateString(boolean isCleaner) {
        int idTarget = DataProcessor.getIdFromCoords(target);
        int idOrigin = DataProcessor.getIdFromCoords(origin);
        String unit = this.unit.getName();
        long arrivalMillis = arrival.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (isCleaner) {
            return idOrigin + "&" + idTarget + "&" + unit + "&" + arrivalMillis + Settings.ULTIMATE_STRING_ZC;
        } else {
            return idOrigin + "&" + idTarget + "&" + unit + "&" + arrivalMillis + Settings.ULTIMATE_STRING_UT;
        }
    }

    public int differenceInSec(Command command) {
        return this.runtime.difference(command.runtime).toSeconds();
    }

    private Runtime calculateRuntime() {
        return Calculator.calculateCommandRuntime(this);
    }

    public boolean isRunning() {
        return runtime.compareTo(Calculator.getTimeToArrival(arrival)) > 0;
    }

    public Point2D getTarget() {
        return target;
    }

    public Point2D getOrigin() {
        return origin;
    }

    public LocalDateTime getArrival() {
        return arrival;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getSenderName() {
        return senderName;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    @Override
    public int compareTo(Command command) {
        return this.runtime.compareTo(command.getRuntime());
    }
}

