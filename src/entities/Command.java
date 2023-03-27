package entities;

import processing.Calculator;
import processing.DataProcessor;
import processing.Settings;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record Command(Point2D target, Point2D origin, LocalDateTime arrival, Unit unit, String senderName) {

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

    public Runtime getRuntime() {
        return Calculator.calculateCommandRuntime(this);
    }

    public boolean isRunning() {
        return getRuntime().compareTo(Calculator.getTimeToArrival(arrival)) > 0;
    }
}

