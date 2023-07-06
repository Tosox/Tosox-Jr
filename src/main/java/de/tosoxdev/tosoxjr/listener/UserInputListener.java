package de.tosoxdev.tosoxjr.listener;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.commands.hangman.HangmanCmd;
import de.tosoxdev.tosoxjr.commands.scramble.ScrambleCmd;
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
