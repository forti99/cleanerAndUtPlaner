package processing;

import entities.Command;
import entities.Runtime;
import entities.Unit;
import entities.Village;
import util.CommandDifferenceComparator;
import util.Settings;

import java.awt.*;
import java.util.*;

public class Calculator {
    private Calculator() {
    }

    public static Set<Command> calculateCleanerOrUt(boolean isCleaner, Set<Command> commandsToClean, Set<Village> startVillages, Set<String> ownerNames, int[] minimumUnits) {
        Set<Village> startVillagesFiltered = filterVillagesForUnits(filterVillagesForOwners(startVillages, ownerNames), minimumUnits);
        Map<Command, ArrayList<Command>> allPotentialCleaner = calculateAllPotentialCleanerOrUtSorted(isCleaner, commandsToClean, startVillagesFiltered, minimumUnits);
        Set<Command> bestCleanerOrUtSet = new HashSet<>();

        Map<Point, Village> locationToFilteredVillage = new HashMap<>();
        for (Village village : startVillagesFiltered) {
            locationToFilteredVillage.put(village.getLocation(), village);
        }

        for (Command commandToClean : allPotentialCleaner.keySet()) {
            ArrayList<Command> potentialCleaner = allPotentialCleaner.get(commandToClean);
            if (potentialCleaner.size() > 0) {
                int i = 0;
                Village bestCleanerOrigin = locationToFilteredVillage.get(potentialCleaner.get(i).getOrigin());
                //takes the best cleaner whose origin village has not yet reached the maximum of allowed cleanerOrUtToSend
                while (bestCleanerOrigin.getCleanerOrUtToSendAmount() >= Settings.MAX_CLEANER_OR_UT_TO_SEND_FROM_VILLAGE && i < potentialCleaner.size() - 1) {
                    i++;
                    bestCleanerOrigin = locationToFilteredVillage.get(potentialCleaner.get(i).getOrigin());
                }
                bestCleanerOrUtSet.add(potentialCleaner.get(i));
                bestCleanerOrigin.addCleanerOrUtToSendAmount(1);
            }
        }
        return bestCleanerOrUtSet;
    }


    public static Map<Command, ArrayList<Command>> calculateAllPotentialCleanerOrUtSorted(boolean isCleaner, Set<Command> commandsToClean, Set<Village> startVillagesFiltered, int[] minimumUnits) {
        Map<Command, ArrayList<Command>> potentialCleanerPerCommands = new HashMap<>();

        Runtime commandRuntime;
        for (Command commandToClean : commandsToClean) {
            commandRuntime = commandToClean.getRuntime();
            ArrayList<Command> potentialCleaners = new ArrayList<>();
            for (Village startVillage : startVillagesFiltered) {
                double distance = commandToClean.getTarget().distance(startVillage.getLocation());

                int[] villageUnits = startVillage.getUnits();

                int j = 0;
                for (int i : villageUnits) {
                    if (i >= minimumUnits[j] && minimumUnits[j] > 0) {
                        //converts a number to a unit for distance-calculating
                        Unit unit = Unit.intToUnit(j);
                        if (isCleaner) {
                            if (calculateRuntime(distance, unit).compareTo(commandRuntime) >= 0) {
                                Command potentialCleanerCommand = new Command(commandToClean.getTarget(), startVillage.getLocation(), commandToClean.getArrival(), unit, startVillage.getOwnerName());
                                potentialCleaners.add(potentialCleanerCommand);
                            }
                        } else {
                            if (calculateRuntime(distance, unit).compareTo(commandRuntime) <= 0) {
                                Command potentialCleanerCommand = new Command(commandToClean.getTarget(), startVillage.getLocation(), commandToClean.getArrival(), unit, startVillage.getOwnerName());
                                potentialCleaners.add(potentialCleanerCommand);
                            }
                        }
                    }
                    j++;
                }
            }
            //Sorts the potential cleaners according to their runtime (and consequentially according to there runtime difference with the command to clean)
            potentialCleaners.sort(new CommandDifferenceComparator(commandToClean));
            potentialCleanerPerCommands.put(commandToClean, potentialCleaners);
        }

        return potentialCleanerPerCommands;
    }

    private static Set<Village> filterVillagesForOwners(Set<Village> villages, Set<String> ownerNames) {
        Set<Village> filteredVillages = new HashSet<>();

        for (Village village : villages) {
            if (ownerNames.contains(village.getOwnerName())) {
                filteredVillages.add(village);
            }
        }
        return filteredVillages;
    }

    private static Set<Village> filterVillagesForUnits(Set<Village> villages, int[] minimumUnits) {
        Set<Village> filteredVillages = new HashSet<>();

        for (Village village : villages) {
            boolean enoughUnits = true;
            for (int i = 0; i < minimumUnits.length; i++) {
                if (village.getUnits()[i] < minimumUnits[i]) {
                    enoughUnits = false;
                    break;
                }
            }
            if (enoughUnits) {
                filteredVillages.add(village);
            }
        }
        return filteredVillages;
    }

    public static Runtime calculateRuntime(double distance, Unit unit) {
        return Runtime.secondsToRuntime((int) Math.round(distance * (unit.getSpeed() / (Settings.WORLDSPEED * Settings.UNIT_MODIFICATOR))));
    }
}
