package de.tosoxdev.tosoxjr.games.hangman;

import de.tosoxdev.tosoxjr.commands.CommandBase;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class HangmanCmd extends CommandBase {
    private final Hangman hangman = new Hangman();

    public HangmanCmd() {
        super("hangman", "Play a game of hangman");
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        if (hangman.isRunning()) {
            event.getChannel().sendMessage("An instance of 'Hangman' is already running").queue();
            return;
        }

        String author = event.getAuthor().getAsTag();
        MessageChannel channel = event.getChannel();
        if (!hangman.newGame(author, channel)) {
            event.getChannel().sendMessage("I'm unable to generate a random word :/").queue();
        }
    }
}
