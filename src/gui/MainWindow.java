package gui;

import entities.Command;
import entities.Village;
import processing.Calculator;
import processing.DataProcessor;
import util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainWindow {
    public JPanel mainPanel;
    private JTextField spearInputField;
    private JTextField spyInputField;
    private JTextField ramInputField;
    private JTextField palaInputField;
    private JTextField swordInputField;
    private JTextField lkavInputField;
    private JTextField cataInputField;
    private JTextField nobleInputField;
    private JTextField axeInputField;
    private JTextField skavInputField;
    private JTextArea importNobleTextArea;
    private JTextArea importTroopsTextArea;
    private JButton cleanerUTCalculateButton;
    private JTextArea inputPlayerNames;
    private JTextField maxCleanerFromVillageField;
    private JRadioButton UTBerechnenRadioButton;
    private JRadioButton cleanerBerechnenRadioButton;
    private JTextField maxCleanerTargetVillageField;

    public MainWindow() {
        cleanerUTCalculateButton.addActionListener(new CalculateButtonPressed());
    }

    public class CalculateButtonPressed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final long timeStart = System.currentTimeMillis();
            ArrayList<Command> commands;

            try {
                commands = DataProcessor.readUltimateCommandsFromTextArea(importNobleTextArea);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            ArrayList<Village> startVillages = DataProcessor.readVillagesFromTextArea(importTroopsTextArea);
            ArrayList<String> senderNames = DataProcessor.readLinesFromTextArea(inputPlayerNames);
            int[] minimumUnits = readMinimumUnits();
            Calculator calculator = new Calculator();
            int maxCleanerFromVillage = getIntFromTextField(maxCleanerFromVillageField);
            int maxCleanerTargetVillage = getIntFromTextField(maxCleanerTargetVillageField);
            boolean isCleaner = cleanerBerechnenRadioButton.isSelected();

            if (maxCleanerFromVillage != 0) {
                Settings.MAX_CLEANER_TO_SEND_FROM_VILLAGE = maxCleanerFromVillage;
            }

            if (maxCleanerTargetVillage != 0) {
                Settings.MAX_CLEANER_TO_SEND_TO_TARGET_VILLAGE = maxCleanerTargetVillage;
            }

            ArrayList<Command> allSenderCommands = calculator.calculateFilteredCleanerOrUT(isCleaner, commands, startVillages, senderNames, minimumUnits);
            ArrayList<ArrayList<Command>> commandsSplitedBySender = DataProcessor.splitCommandsForOwners(allSenderCommands, senderNames);
            try {
                displayUltimateCommandOutput(isCleaner, commandsSplitedBySender);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            final long timeEnd = System.currentTimeMillis();
            System.out.println("Runtime: " + (timeEnd - timeStart) + " Millisek.");
        }
    }

    private void displayUltimateCommandOutput(boolean isCleaner, ArrayList<ArrayList<Command>> allOwnersUltimateCommands) throws IOException {
        JFrame outputFrame = new JFrame("Ausgabe Zwischencleaner");
        outputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();

        int amountAllCommands = 0;
        for (ArrayList<Command> ownerCommands : allOwnersUltimateCommands) {
            amountAllCommands += ownerCommands.size();
        }
        JTextArea allCommandsTextArea = addTab(tabbedPane, "Alle Befehle" + " (" + amountAllCommands + ")");

        allCommandsTextArea.setPreferredSize(new Dimension(1100, 800));
        if (allOwnersUltimateCommands.size() == 0) {
            allCommandsTextArea.append("Keine Cleaner berechnet!");
        } else {
            ArrayList<String> ownerNames = DataProcessor.readLinesFromTextArea(inputPlayerNames);
            int i = 0;
            for (ArrayList<Command> ownerCommands : allOwnersUltimateCommands) {
                if (ownerCommands.size() != 0) {
                    JTextArea playerTextArea = addTab(tabbedPane, ownerNames.get(i) + " (" + ownerCommands.size() + ")");
                    for (Command command : ownerCommands) {
                        allCommandsTextArea.append(command.toUltimateString(isCleaner) + "\n");
                        playerTextArea.append(command.toUltimateString(isCleaner) + "\n");
                        amountAllCommands++;
                    }
                }
                i++;
            }
        }

        outputFrame.setLocationRelativeTo(null);
        outputFrame.setLocation(outputFrame.getX() - mainPanel.getWidth() / 2, outputFrame.getY() - mainPanel.getHeight() / 2);
        outputFrame.getContentPane().add(tabbedPane);
        outputFrame.pack();
        outputFrame.setVisible(true);
    }

    private JTextArea addTab(JTabbedPane tabbedPane, String tabName) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        JButton copyButton = new JButton("In Zwischenablage kopieren");
        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(textArea.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        });

        panel.add(textArea, BorderLayout.CENTER);
        panel.add(copyButton, BorderLayout.SOUTH);
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(1100, 800));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab(tabName, scrollPane);
        return textArea;
    }

    private int[] readMinimumUnits() {
        int[] minimumUnits = new int[10];

        minimumUnits[0] = getIntFromTextField(spearInputField);
        minimumUnits[1] = getIntFromTextField(swordInputField);
        minimumUnits[2] = getIntFromTextField(axeInputField);
        minimumUnits[3] = getIntFromTextField(spyInputField);
        minimumUnits[4] = getIntFromTextField(lkavInputField);
        minimumUnits[5] = getIntFromTextField(skavInputField);
        minimumUnits[6] = getIntFromTextField(ramInputField);
        minimumUnits[7] = getIntFromTextField(cataInputField);
        minimumUnits[8] = getIntFromTextField(palaInputField);
        minimumUnits[9] = getIntFromTextField(nobleInputField);

        return minimumUnits;
    }

    private int getIntFromTextField(JTextField textField) {
        int intValue;

        String unitAmountText = textField.getText();
        if (Objects.equals(unitAmountText, "")) {
            intValue = 0;
        } else {
            intValue = Integer.parseInt(textField.getText());
        }
        return intValue;
    }
}


