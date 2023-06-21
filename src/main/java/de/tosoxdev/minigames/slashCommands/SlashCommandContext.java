package de.tosoxdev.minigames.slashCommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public record SlashCommandContext(SlashCommandInteractionEvent event) implements ISlashCommandContext { }
