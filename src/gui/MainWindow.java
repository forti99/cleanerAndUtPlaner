package gui;

import entities.Command;
import entities.Village;
import processing.Calculator;
import processing.DataProcessor;

import javax.swing.*;
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
    private JTextArea outputTextArea;
    private JButton cleanerUTCalculateButton;
    private JRadioButton cleanerPlanningRadioButton;
    private JRadioButton utPlanningRadioButton;
    private JTextArea inputPlayerNames;

    public MainWindow() {
        cleanerUTCalculateButton.addActionListener(new CalculateButtonPressed());
    }

    private class CalculateButtonPressed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Command> commands;
            try {
                commands = DataProcessor.readUltimateCommandsFromTextArea(importNobleTextArea);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ArrayList<Village> startVillages = DataProcessor.readVillagesFromTextArea(importTroopsTextArea);
            ArrayList<String> senderNames = DataProcessor.readLinesFromTextArea(inputPlayerNames);
            int[] minimumUnits = readMinimumUnits();
            boolean isCleaner = readIsCleaner();
            Calculator calculator = new Calculator();


            ArrayList<Command> allSenderCommands = calculator.calculateFilteredCleanerOrUT(isCleaner, commands, startVillages, senderNames, minimumUnits);
            ArrayList<ArrayList<Command>> commandsSplitedBySender = DataProcessor.splitCommandsForOwners(allSenderCommands, senderNames);
            try {
                displayUltimateCommandOutput(commandsSplitedBySender);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void displayUltimateCommandOutput(ArrayList<ArrayList<Command>> allOwnersUltimateCommands) throws IOException {
        outputTextArea.setText("");
        ArrayList<String> ownerNames = DataProcessor.readLinesFromTextArea(inputPlayerNames);
        int i = 0;
        for (ArrayList<Command> ownerCommands : allOwnersUltimateCommands) {
            outputTextArea.append(ownerNames.get(i) + "\n");
            if (ownerCommands.size() == 0) {
                outputTextArea.append("---\n");
            } else {
                for (Command command : ownerCommands) {
                    outputTextArea.append(command.toUltimateString(cleanerPlanningRadioButton.isSelected()) + "\n");
                }
            }
            outputTextArea.append("\n");
            i++;
        }
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
        int unitAmount;

        String unitAmountText = textField.getText();
        if (Objects.equals(unitAmountText, "")) {
            unitAmount = 0;
        } else {
            unitAmount = Integer.parseInt(textField.getText());
        }
        return unitAmount;
    }

    private boolean readIsCleaner() {
        return cleanerPlanningRadioButton.isSelected();
    }
}
