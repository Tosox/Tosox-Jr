package de.tosoxdev.tosoxjr.utils;

import de.tosoxdev.tosoxjr.commands.CommandBase;

import java.util.Comparator;

public class CommandSorterByName implements Comparator<CommandBase> {
    @Override
    public int compare(CommandBase cmd1, CommandBase cmd2) {
        return cmd1.getName().compareTo(cmd2.getName());
    }
}
