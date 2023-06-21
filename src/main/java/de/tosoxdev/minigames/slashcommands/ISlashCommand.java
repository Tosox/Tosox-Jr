package de.tosoxdev.minigames.slashcommands;

public interface ISlashCommand {
    void handle(SlashCommandContext slashCommandContext);
    String getName();
    String getHelp();
}
