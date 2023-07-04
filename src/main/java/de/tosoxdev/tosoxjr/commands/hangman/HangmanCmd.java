package de.tosoxdev.tosoxjr.commands.hangman;

import de.tosoxdev.tosoxjr.commands.GameBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;

public class HangmanCmd extends GameBase {
    private final HashMap<String, Hangman> games = new HashMap<>();
    private static HangmanCmd instance;

    public HangmanCmd() {
        super("hangman", "Play a game of hangman", null);
        instance = this;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String user = event.getUser().getAsTag();
        if (games.containsKey(user)) {
            event.reply("An instance of 'hangman' is already running").queue();
            return;
        }

        event.deferReply().queue(m -> m.deleteOriginal().queue());

        Hangman hangman = new Hangman(user, event.getChannel());
        if (hangman.initialize()) {
            games.put(user, hangman);
        }
    }

    @Override
    public void handleEvent(Event event) {
        for (Hangman hangman : games.values()) {
            hangman.handleEvent(event);
        }
    }

    public void removePlayer(String user) {
        games.remove(user);
    }

    public static HangmanCmd getInstance() {
        return instance;
    }
}
