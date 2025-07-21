package de.tosox.tosoxjr.listener;

import de.tosox.tosoxjr.Main;
import de.tosox.tosoxjr.commands.hangman.HangmanCmd;
import de.tosox.tosoxjr.commands.scramble.ScrambleCmd;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class UserInputListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Main.getCommandManager().handle(event);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        HangmanCmd.getInstance().handleEvent(event);
        ScrambleCmd.getInstance().handleEvent(event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        ScrambleCmd.getInstance().handleEvent(event);
    }
}
