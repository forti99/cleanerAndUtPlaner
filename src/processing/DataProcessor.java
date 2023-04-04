package processing;

import entities.Command;
import entities.Unit;
import entities.Village;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;

public class DataProcessor {

    private static ArrayList<String> allVillagesInfoLines;

    static {
        try {
            allVillagesInfoLines = fetchAllVillageInfo();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DataProcessor() throws IOException {
        allVillagesInfoLines = fetchAllVillageInfo();
    }

    public static ArrayList<ArrayList<Command>> splitCommandsForOwners(ArrayList<Command> mixedOwnerCommands, ArrayList<String> ownerNames) {
        ArrayList<ArrayList<Command>> splitedCommands = new ArrayList<>();
        ArrayList<Command> singleOwnerCommands;

        for (String ownerName : ownerNames) {
            singleOwnerCommands = new ArrayList<>();
            for (Command command : mixedOwnerCommands) {
                if (ownerName.equals(command.senderName())) {
                    singleOwnerCommands.add(command);
                }
            }
            splitedCommands.add(singleOwnerCommands);
        }
        return splitedCommands;
    }

    public static ArrayList<Village> readVillagesFromTextArea(JTextArea textArea) {

        ArrayList<String> villagesAndTroopsLines = readLinesFromTextArea(textArea);

        //removes the first line (header line from the used script)
        villagesAndTroopsLines.remove(0);
        ArrayList<Village> villages = new ArrayList<>();
        for (String line : villagesAndTroopsLines) {
            String[] fields = line.split(",");
            String[] locationFields = fields[0].split("\\|");
            String ownerName = fields[1];
            double x = Double.parseDouble(locationFields[0]);
            double y = Double.parseDouble(locationFields[1]);
            int[] units = new int[10];
            for (int i = 0; i < 10; i++) {
                units[i] = Integer.parseInt(fields[i + 2]);
            }
            Village village = new Village(ownerName, x, y, units, 0);
            villages.add(village);
        }
        return villages;
    }

    public static ArrayList<String> fetchAllVillageInfo() throws IOException {
        ArrayList<String> villageInfoLines = new ArrayList<>();

        URL url = new URL("https://de208.die-staemme.de/map/village.txt");
        URLConnection conn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            villageInfoLines.add(inputLine);
        }
        in.close();

        return villageInfoLines;
    }

    public static ArrayList<Command> readUltimateCommandsFromTextArea(JTextArea textArea) throws IOException {
        ArrayList<Command> commands = new ArrayList<>();
        int idOriginVillage;
        int idTargetVillage;
        String unitString;
        long millisArrival;

        ArrayList<String> commandStrings = readLinesFromTextArea(textArea);

        for (String i : commandStrings) {
            String[] parts = i.split("&");
            idOriginVillage = Integer.parseInt(parts[0]);
            idTargetVillage = Integer.parseInt(parts[1]);
            unitString = parts[2];
            millisArrival = Long.parseLong(parts[3]);

            Point2D origin = getLocationFromVillageId(idOriginVillage);
            Point2D target = getLocationFromVillageId(idTargetVillage);
            Unit unit = Unit.ultimateStringToUnit(unitString);
            LocalDateTime arrival = Instant.ofEpochMilli(millisArrival).atZone(ZoneId.systemDefault()).toLocalDateTime();
            //TODO replace placeholder with real name taken from a method that gets the name from the sender
            commands.add(new Command(target, origin, arrival, unit, "PLACEHOLDER"));
        }
        return commands;
    }

    public static ArrayList<String> readLinesFromTextArea(JTextArea textArea) {
        String text = textArea.getText();
        String[] textArray = text.split("\\r?\\n"); // split text by newline

        return new ArrayList<>(Arrays.asList(textArray));
    }

    public static Point2D getLocationFromVillageId(int villageId) {
        String wantedLine = "";
        for (String line : allVillagesInfoLines) {
            if (line.matches("^" + villageId + "\\D.*")) {
                wantedLine = line;
                break;
            }
        }

        String[] parts = wantedLine.split(",");
        double x = Double.parseDouble(parts[2]);
        double y = Double.parseDouble(parts[3]);
        return new Point2D.Double(x, y);
    }

    public static int getIdFromCoords(Point2D coords) {
        int x = (int) coords.getX();
        int y = (int) coords.getY();

        String wantedLine = "";
        for (String line : allVillagesInfoLines) {
            if (line.matches("^.*" + x + "," + y + ".*$")) {
                wantedLine = line;
                break;
            }
        }
        String[] parts = wantedLine.split(",");
        return Integer.parseInt(parts[0]);
    }

    public static ArrayList<Village> filterVillagesForOwnersAndUnits(ArrayList<Village> villages, ArrayList<String> ownerNames, int[] minimumUnits) {
        ArrayList<Village> filteredVillages = filterVillagesForOwners(villages, ownerNames);
        return filterVillagesForUnits(filteredVillages, minimumUnits);
    }

    private static ArrayList<Village> filterVillagesForOwners(ArrayList<Village> villages, ArrayList<String> ownerNames) {
        ArrayList<Village> filteredVillages = new ArrayList<>();

        for (Village village : villages) {
            if (ownerNames.contains(village.getOwnerName())) {
                filteredVillages.add(village);
            }
        }
        return filteredVillages;
    }

    private static ArrayList<Village> filterVillagesForUnits(ArrayList<Village> villages, int[] minimumUnits) {
        ArrayList<Village> filteredVillages = new ArrayList<>();

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
}
