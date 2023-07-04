package de.tosoxdev.tosoxjr.commands.hangman;

import de.tosoxdev.tosoxjr.commands.GameBase;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.List;

public class HangmanCmd extends GameBase {
    private final HashMap<String, Hangman> games = new HashMap<>();
    private static HangmanCmd instance;

    public HangmanCmd() {
        super("hangman", "Play a game of hangman", List.of(
                new OptionData(OptionType.BOOLEAN, "coop", "Play with all you friends on the server", false)
        ));
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

        boolean coop = ArgumentParser.getBoolean(event.getOption("coop"), false);
        Hangman hangman = new Hangman(user, event.getChannel(), coop);
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
