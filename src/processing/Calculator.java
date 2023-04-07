package processing;

import entities.Command;
import entities.Runtime;
import entities.Unit;
import entities.Village;
import util.CommandComparator;
import util.Settings;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Calculator {

    public ArrayList<Command> calculateFilteredCleanerOrUT(boolean isCleaner, ArrayList<Command> allCommandsToCleanOrUT, ArrayList<Village> startingVillages, ArrayList<String> ownerNames, int[] minimumUnits) {

        Runtime maxRuntime = Settings.MAX_RUNTIME_START;
        ArrayList<Village> allOwnersVillages = DataProcessor.filterVillagesForOwnersAndUnits(startingVillages, ownerNames, minimumUnits);
        ArrayList<Command> allCleanerOrUTList = new ArrayList<>();
        int neededCleanerAmount = allCommandsToCleanOrUT.size() * Settings.MAX_CLEANER_TO_SEND_TO_TARGET_VILLAGE;

        while (allCleanerOrUTList.size() < neededCleanerAmount) {
            ArrayList<Command> commandsWithFoundCleaners = new ArrayList<>();
            for (Command commandToCleanOrUT : allCommandsToCleanOrUT) {
                ArrayList<Command> cleanerOrUTList = new ArrayList<>();
                for (Village startVillage : allOwnersVillages) {
                    cleanerOrUTList.addAll(calculateSingleCleanerOrUT(isCleaner, commandToCleanOrUT, startVillage, minimumUnits, maxRuntime, startVillage.getOwnerName()));
                }
                if (cleanerOrUTList.size() > 0) {
                    Village startVillageWithMaxCleaner = null;
                    List<Command> closestCommands = findClosestCommands(isCleaner, commandToCleanOrUT, cleanerOrUTList);
                    for (Village startVillage : allOwnersVillages) {
                        for (Command closestCommand : closestCommands) {
                            if (startVillage.getLocation().equals(closestCommand.getOrigin())) {
                                startVillage.setCleanerOrUtToSendAmount(startVillage.getCleanerOrUtToSendAmount() + 1);
                                if (startVillage.getCleanerOrUtToSendAmount() >= Settings.MAX_CLEANER_TO_SEND_FROM_VILLAGE) {
                                    startVillageWithMaxCleaner = startVillage;
                                }
                                break;
                            }
                        }
                    }
                    allOwnersVillages.remove(startVillageWithMaxCleaner);
                    allCleanerOrUTList.addAll(closestCommands);
                    commandsWithFoundCleaners.add(commandToCleanOrUT);
                }
            }
            for (Command commandWithFoundCleaner : commandsWithFoundCleaners) {
                allCommandsToCleanOrUT.remove(commandWithFoundCleaner);
            }
            maxRuntime.addSeconds(Settings.RUNTIME_STEP_INCREASE_IN_SECONDS);
            if (maxRuntime.compareTo(Settings.MAX_RUNTIME_DIFFERENCE) > 0) {
                break;
            }
        }
        return allCleanerOrUTList;
    }

    private ArrayList<Command> calculateSingleCleanerOrUT(boolean isCleaner, Command command, Village startVillage, int[] minimumUnits, Runtime maxRuntimeDifference, String senderName) {
        ArrayList<Command> cleanerOrUTList = new ArrayList<>();

        if (startVillage.getCleanerOrUtToSendAmount() >= Settings.MAX_CLEANER_TO_SEND_FROM_VILLAGE) {
            return cleanerOrUTList;
        }

        Runtime commandRuntime;

        if (command.isRunning()) {
            commandRuntime = getTimeToArrival(command.getArrival());
        } else {
            commandRuntime = command.getRuntime();
        }

        double distance = calculateDistance(command.getTarget(), startVillage.getLocation());

        int[] villageUnits = startVillage.getUnits();

        int j = 0;
        for (int i : villageUnits) {
            if (i >= minimumUnits[j] && minimumUnits[j] > 0) {
                //converts a number to a unit for distance-calculating
                Unit unit = Unit.intToUnit(j);
                Runtime runtime = calculateRuntime(distance, unit);
                if (isCleaner) {
                    if (runtime.compareTo(commandRuntime) >= 0 && runtime.difference(commandRuntime).getPositiveRuntime().compareTo(maxRuntimeDifference) <= 0) {
                        cleanerOrUTList.add(new Command(command.getTarget(), startVillage.getLocation(), command.getArrival(), unit, senderName));
                    }
                } else {
                    if (runtime.compareTo(commandRuntime) <= 0 && runtime.difference(commandRuntime).getPositiveRuntime().compareTo(maxRuntimeDifference) <= 0) {
                        cleanerOrUTList.add(new Command(command.getTarget(), startVillage.getLocation(), command.getArrival(), unit, senderName));
                    }
                }
            }
            j++;
        }
        return cleanerOrUTList;
    }

    private List<Command> findClosestCommands(boolean isCleaner, Command referenceCommand, ArrayList<Command> commandsToSearch) {
        commandsToSearch.sort(new CommandComparator(referenceCommand));
        if (isCleaner) {
            if (commandsToSearch.size() - Settings.MAX_CLEANER_TO_SEND_TO_TARGET_VILLAGE < 0) {
                return commandsToSearch;
            } else {
                return commandsToSearch.subList(commandsToSearch.size() - Settings.MAX_CLEANER_TO_SEND_TO_TARGET_VILLAGE, commandsToSearch.size());
            }
        } else {
            return commandsToSearch.subList(0, commandsToSearch.size());
        }
    }

    public static Runtime calculateCommandRuntime(Command command) {
        double distance = calculateDistance(command.getTarget(), command.getOrigin());
        return calculateRuntime(distance, command.getUnit());
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
