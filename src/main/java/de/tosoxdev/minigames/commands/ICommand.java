package de.tosoxdev.minigames.commands;

public interface ICommand {
    void handle(CommandContext commandContext);
    String getName();
    String getHelp();
}
