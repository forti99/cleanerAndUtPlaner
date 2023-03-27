package processing;

import entities.Command;
import entities.Runtime;
import entities.Unit;
import entities.Village;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Calculator {

    public ArrayList<Command> calculateFilteredCleanerOrUT(boolean isCleaner, ArrayList<Command> allCommandsToCleanOrUT, ArrayList<Village> startingVillages, ArrayList<String> ownerNames, int[] minimumUnits) {

        Runtime maxRuntime = Settings.RUNTIME_STEP_INCREASE;
        ArrayList<Village> allOwnersVillages = DataProcessor.filterVillagesForOwnersAndUnits(startingVillages, ownerNames, minimumUnits);
        ArrayList<Command> allCleanerOrUTList = new ArrayList<>();

        while (allCleanerOrUTList.size() < allCommandsToCleanOrUT.size()) {
            allCleanerOrUTList = new ArrayList<>();
            for (Command commandToCleanOrUT : allCommandsToCleanOrUT) {
                ArrayList<Command> cleanerOrUTList = new ArrayList<>();
                for (Village startVillage : allOwnersVillages) {
                    cleanerOrUTList.addAll(calculateSingleCleanerOrUT(isCleaner, commandToCleanOrUT, startVillage, minimumUnits, maxRuntime, startVillage.getOwnerName()));
                }
                if (cleanerOrUTList.size() > 0) {
                    allCleanerOrUTList.add(findClosestCommand(commandToCleanOrUT, cleanerOrUTList));
                }

            }
            maxRuntime.addMinutes(1);
        }

        return allCleanerOrUTList;
    }

    private ArrayList<Command> calculateSingleCleanerOrUT(boolean isCleaner, Command command, Village startVillage, int[] minimumUnits, Runtime maxRuntimeDifference, String senderName) {
        Point2D origin = startVillage.getLocation();
        Point2D target = command.target();
        Runtime commandRuntime;

        if (command.isRunning()) {
            commandRuntime = getTimeToArrival(command.arrival());
        } else {
            commandRuntime = command.getRuntime();
        }

        double distance = calculateDistance(target, origin);

        ArrayList<Command> cleanerOrUTList = new ArrayList<>();
        int[] villageUnits = startVillage.getUnits();
        int j = 0;
        for (int i : villageUnits) {
            if (i >= minimumUnits[j] && minimumUnits[j] > 0) {
                //converts a number to a unit for distance-calculating
                Unit unit = Unit.intToUnit(j);
                Runtime runtime = calculateRuntime(distance, unit);
                if (isCleaner) {
                    if (runtime.compareTo(commandRuntime) >= 0) {
                        Runtime differenceToOtherCommand = runtime.difference(commandRuntime);
                        if (differenceToOtherCommand.getPositiveRuntime().compareTo(maxRuntimeDifference) <= 0) {
                            cleanerOrUTList.add(new Command(target, origin, command.arrival(), unit, senderName));
                        }
                    }
                } else {
                    if (runtime.compareTo(commandRuntime) <= 0) {
                        Runtime differenceToOtherCommand = runtime.difference(commandRuntime);
                        if (differenceToOtherCommand.getPositiveRuntime().compareTo(maxRuntimeDifference) <= 0) {
                            cleanerOrUTList.add(new Command(target, origin, command.arrival(), unit, senderName));
                        }
                    }
                }
            }
            j++;
        }
        return cleanerOrUTList;
    }

    private Command findClosestCommand(Command referenceCommand, ArrayList<Command> commandsToSearch) {
        //Dummy- Initialization
        Command closestCommand = new Command(new Point2D.Double(0, 0), new Point2D.Double(9999, 9999), LocalDateTime.now(), Unit.NOBLE, "DUMMYUSER");

        for (Command command : commandsToSearch) {
            int differenceClosestCommand = Math.abs(referenceCommand.getRuntime().difference(closestCommand.getRuntime()).toSeconds());
            int difference = Math.abs(referenceCommand.getRuntime().difference(command.getRuntime()).toSeconds());
            if (difference < differenceClosestCommand) {
                closestCommand = command;
            }
        }

        return closestCommand;
    }

    public static Runtime calculateCommandRuntime(Command command) {
        double distance = calculateDistance(command.target(), command.origin());
        return Runtime.secondsToRuntime((int) Math.round(distance * (command.unit().getSpeed() / (Settings.WORLDSPEED * Settings.UNIT_MODIFICATOR))));
    }

    public static Runtime calculateRuntime(double distance, Unit unit) {
        return Runtime.secondsToRuntime((int) Math.round(distance * (unit.getSpeed() / (Settings.WORLDSPEED * Settings.UNIT_MODIFICATOR))));
    }

    private static double calculateDistance(Point2D target, Point2D origin) {
        return target.distance(origin.getX(), origin.getY());
    }

    public static Runtime getTimeToArrival(LocalDateTime arrival) {
        LocalDateTime now = LocalDateTime.now();
        int diff = (int) now.until(arrival, ChronoUnit.SECONDS);
        return Runtime.secondsToRuntime(diff);
    }
}
