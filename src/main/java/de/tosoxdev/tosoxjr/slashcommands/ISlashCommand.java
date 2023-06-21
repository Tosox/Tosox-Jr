package de.tosoxdev.tosoxjr.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ISlashCommand {
    void handle(SlashCommandInteractionEvent event);
    String getName();
    String getDescription();
}
