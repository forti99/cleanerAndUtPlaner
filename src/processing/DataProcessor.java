package processing;

import entities.Command;
import entities.Unit;
import entities.Village;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class DataProcessor {
    private static final Map<Point, Integer> locationToId = new HashMap<>();
    private static final Map<Integer, Point> idToLocation = new HashMap<>();
    private static final Set<String> potentialSenderNames = new HashSet<>();

    private DataProcessor() {
    }

    public static Set<Set<Command>> splitCommandsForOwners(Set<Command> mixedOwnerCommands, Set<String> ownerNames) {
        Set<Set<Command>> splitedCommands = new HashSet<>();
        Set<Command> singleOwnerCommands;

        for (String ownerName : ownerNames) {
            singleOwnerCommands = new HashSet<>();
            for (Command command : mixedOwnerCommands) {
                if (ownerName.equals(command.getSenderName())) {
                    singleOwnerCommands.add(command);
                }
            }
            splitedCommands.add(singleOwnerCommands);
        }
        return splitedCommands;
    }

    public static Set<Village> readVillagesFromTextArea(JTextArea textArea) {

        Set<String> villagesAndTroopsLines = readLinesFromTextArea(textArea);

        //removes the first line (header line from the used script)
        villagesAndTroopsLines.remove("Coords,Player,spear,sword,axe,spy,light,heavy,ram,catapult,knight,snob,");
        Set<Village> villages = new HashSet<>();
        for (String line : villagesAndTroopsLines) {
            String[] fields = line.split(",");
            String[] locationFields = fields[0].split("\\|");
            String ownerName = fields[1];
            int x = Integer.parseInt(locationFields[0]);
            int y = Integer.parseInt(locationFields[1]);
            int[] units = new int[10];
            for (int i = 0; i < 10; i++) {
                units[i] = Integer.parseInt(fields[i + 2]);
            }
            potentialSenderNames.add(ownerName);
            Village village = new Village(ownerName, new Point(x, y), units, 0);
            villages.add(village);
        }
        return villages;
    }

    public static void fetchAllVillageInfo() throws IOException {
        URL url = new URL("https://de208.die-staemme.de/map/village.txt");
        URLConnection conn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            String[] parts = inputLine.split(",");
            int id = Integer.parseInt(parts[0]);
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            locationToId.put(new Point(x, y), id);
            idToLocation.put(id, new Point(x, y));
        }
        in.close();
    }

    public static Set<Command> readUltimateCommandsFromTextArea(JTextArea textArea) throws IOException {
        Set<Command> commands = new HashSet<>();
        int idOriginVillage;
        int idTargetVillage;
        String unitString;
        long millisArrival;

        Set<String> commandStrings = readLinesFromTextArea(textArea);

        for (String i : commandStrings) {
            String[] parts = i.split("&");
            idOriginVillage = Integer.parseInt(parts[0]);
            idTargetVillage = Integer.parseInt(parts[1]);
            unitString = parts[2];
            millisArrival = Long.parseLong(parts[3]);

            Point origin = idToLocation.get(idOriginVillage);
            Point target = idToLocation.get(idTargetVillage);
            Unit unit = Unit.ultimateStringToUnit(unitString);
            LocalDateTime arrival = Instant.ofEpochMilli(millisArrival).atZone(ZoneId.systemDefault()).toLocalDateTime();
            //TODO replace placeholder with real name taken from a method that gets the name from the sender
            commands.add(new Command(target, origin, arrival, unit, "PLACEHOLDER"));
        }
        return commands;
    }

    public static Set<String> readLinesFromTextArea(JTextArea textArea) {
        String text = textArea.getText();
        String[] textArray = text.split("\\r?\\n"); // split text by newline

        return new HashSet<>(Arrays.asList(textArray));
    }

    public static ArrayList<String> readOwnerNamesFromTextArea(JTextArea textArea) {
        String text = textArea.getText();
        String[] textArray = text.split("\\r?\\n"); // split text by newline
        return new ArrayList<>(Arrays.asList(textArray));
    }

    public static int getIdFromCoords(Point coords) {
        return locationToId.get(coords);
    }
}
