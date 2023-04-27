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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private JTextField maxCleanerFromVillageField;
    private JRadioButton UTBerechnenRadioButton;
    private JRadioButton cleanerBerechnenRadioButton;
    private JButton spielerAuswaehlenButton;
    private JLabel senderSelectionLabel;
    private JTextField cleanerPerTargetField;
    private JTextArea senderNamesTextArea;
    private JTextField utBoostField;
    private Set<String> potentialSenderNames;
    private Set<Village> startVillages;
    Set<String> senderNames = new HashSet<>();

    public MainWindow() {
        spielerAuswaehlenButton.addActionListener(new EinlesenButtonPressed());
        cleanerUTCalculateButton.addActionListener(new CalculateButtonPressed());
    }

    public class EinlesenButtonPressed implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            startVillages = DataProcessor.readVillagesFromTextArea(importTroopsTextArea);
            potentialSenderNames = DataProcessor.getPotentialSenderNames();
            addCheckboxesForPotentialSenders();
        }
    }

    public class CalculateButtonPressed implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean isCleaner = cleanerBerechnenRadioButton.isSelected();

            int[] minimumUnits = readMinimumUnits();
            int maxCleanerFromVillage = getIntFromTextField(maxCleanerFromVillageField);
            int cleanerPerVillage = getIntFromTextField(cleanerPerTargetField);
            double utBoostPercentage = getIntFromTextField(utBoostField);
            Settings.UT_BOOST_FACTOR = 1 - utBoostPercentage;

            Set<Command> commands;
            try {
                commands = DataProcessor.readUltimateCommandsFromTextArea(importNobleTextArea);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            startVillages = DataProcessor.readVillagesFromTextArea(importTroopsTextArea);
            senderNames = new HashSet<>(DataProcessor.readOwnerNamesFromTextArea(senderNamesTextArea));

            if (maxCleanerFromVillage > 0) {
                Settings.MAX_CLEANER_OR_UT_TO_SEND_FROM_VILLAGE = maxCleanerFromVillage;
            }
            if (cleanerPerVillage > 0) {
                Settings.CLEANER_PER_VILLAGE = cleanerPerVillage;
            }

            Set<Command> allSenderCommands = Calculator.calculateCleanerOrUt(isCleaner, commands, startVillages, senderNames, minimumUnits);
            Set<Set<Command>> commandsSplitedBySender = DataProcessor.splitCommandsForOwners(allSenderCommands, senderNames);
            try {
                displayUltimateCommandOutput(isCleaner, commandsSplitedBySender);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void displayUltimateCommandOutput(boolean isCleaner, Set<Set<Command>> allOwnersUltimateCommands) throws IOException {
        JFrame outputFrame = new JFrame("Ausgabe Zwischencleaner");
        outputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();

        int amountAllCommands = 0;
        for (Set<Command> ownerCommands : allOwnersUltimateCommands) {
            amountAllCommands += ownerCommands.size();
        }
        JTextArea allCommandsTextArea = addTab(tabbedPane, "Alle Befehle" + " (" + amountAllCommands + ")");

        allCommandsTextArea.setPreferredSize(new Dimension(1100, 800));
        if (allOwnersUltimateCommands.size() == 0) {
            allCommandsTextArea.append("Keine Cleaner berechnet!");
        } else {
            ArrayList<String> ownerNames = new ArrayList<>(senderNames);
            int i = 0;
            for (Set<Command> ownerCommands : allOwnersUltimateCommands) {
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

    private void addCheckboxesForPotentialSenders() {
        JFrame senderSelectionFrame = new JFrame("Absender");
        senderSelectionFrame.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JScrollPane scrollPane = new JScrollPane(panel);
        senderSelectionFrame.add(scrollPane, BorderLayout.CENTER);

        JLabel senderSelectionLabel = new JLabel("Absender auswählen");
        panel.add(senderSelectionLabel);

        for (String sender : potentialSenderNames) {
            JCheckBox checkBox = new JCheckBox(sender);
            panel.add(checkBox);
        }

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            StringBuilder selectedSenders = new StringBuilder();
            Component[] components = panel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox checkBox) {
                    if (checkBox.isSelected()) {
                        selectedSenders.append(checkBox.getText()).append("\n");
                    }
                }
            }
            senderNamesTextArea.setText(selectedSenders.toString());
            senderSelectionFrame.dispose();
        });

        panel.add(okButton);

        // Erhalten der Position des aktuellen Frames
        Point location = mainPanel.getLocation();

        // Berechnung der Position des neuen Frames relativ zum aktuellen Frame
        int x = location.x + (mainPanel.getWidth() - senderSelectionFrame.getWidth()) / 2;
        int y = location.y + (mainPanel.getHeight() - senderSelectionFrame.getHeight()) / 2;

        // Setzen der Position des neuen Frames
        senderSelectionFrame.setLocation(x, y);
        senderSelectionFrame.pack();
        senderSelectionFrame.setVisible(true);
    }
}



