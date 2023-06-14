package de.tosoxdev.minigames.utils;

import de.tosoxdev.minigames.commands.ICommand;

import java.util.Comparator;

public class CommandSorterByName implements Comparator<ICommand> {
    @Override
    public int compare(ICommand cmd1, ICommand cmd2) {
        return cmd1.getName().compareTo(cmd2.getName());
    }
}
