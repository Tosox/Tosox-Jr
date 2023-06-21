package de.tosoxdev.tosoxjr.utils;

import de.tosoxdev.tosoxjr.commands.ICommand;

import java.util.Comparator;

public class CommandSorterByName implements Comparator<ICommand> {
    @Override
    public int compare(ICommand cmd1, ICommand cmd2) {
        return cmd1.getName().compareTo(cmd2.getName());
    }
}
