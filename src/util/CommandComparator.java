package util;

import entities.Command;

import java.util.Comparator;

public class CommandComparator implements Comparator<Command> {
    private final Command command;

    public CommandComparator(Command command) {
        this.command = command;
    }

    @Override
    public int compare(Command c1, Command c2) {
        int diff1 = Math.abs(c1.differenceInSec(command));
        int diff2 = Math.abs(c2.differenceInSec(command));
        return Integer.compare(diff1, diff2);
    }
}