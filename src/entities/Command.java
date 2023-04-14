package entities;

import processing.DataProcessor;
import util.Settings;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Command implements Comparable<Command> {
    private final Point target;
    private final Point origin;
    private final LocalDateTime arrival;
    private final Unit unit;
    private final String senderName;
    private final Runtime runtime;

    public Command(Point target, Point origin, LocalDateTime arrival, Unit unit, String senderName) {
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
        return Runtime.secondsToRuntime((int) Math.round(target.distance(origin) * (unit.getSpeed() / (Settings.WORLDSPEED * Settings.UNIT_MODIFICATOR))));
    }

    public Point getTarget() {
        return target;
    }

    public Point getOrigin() {
        return origin;
    }

    public LocalDateTime getArrival() {
        return arrival;
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

