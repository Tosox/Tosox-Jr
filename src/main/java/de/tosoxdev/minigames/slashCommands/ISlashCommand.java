package de.tosoxdev.minigames.slashCommands;

public interface ISlashCommand {
    void handle(SlashCommandContext slashCommandContext);
    String getName();
    String getHelp();
}
