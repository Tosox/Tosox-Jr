package de.tosox.tosoxjr.listener;

import de.tosox.tosoxjr.command.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {
    private final CommandManager commandManager;

    public SlashCommandListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commandManager.handleCommand(event);
    }
}
