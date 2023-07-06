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
    private static final int MAX_GAMES = 10;
    private static HangmanCmd instance;
    private final HashMap<String, Hangman> games = new HashMap<>();
    private final String languages;

    public HangmanCmd() {
        super("hangman", "Play a game of Hangman", List.of(
                new OptionData(OptionType.STRING, "lang", "Decide the langauge of the word. Use 'list' to list all available ones", false),
                new OptionData(OptionType.BOOLEAN, "coop", "Play Hangman with all your friends on the server", false)
        ));
        instance = this;

        StringBuilder sb = new StringBuilder("Available languages");
        Hangman.RANDOM_WORD_APIS.forEach((key, value) -> sb.append(String.format("\n- %s", key)));
        languages = sb.toString();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String lang = ArgumentParser.getString(event.getOption("lang"), "");
        if (lang.equalsIgnoreCase("list")) {
            event.reply(languages).queue();
            return;
        }

        String user = event.getUser().getAsTag();
        if (games.containsKey(user)) {
            event.reply("You already started a game of Hangman").queue();
            return;
        }

        if (games.size() > MAX_GAMES) {
            event.reply("Sorry, there are to many games of Hangman already").queue();
            return;
        }

        event.deferReply().queue(m -> m.deleteOriginal().queue());

        boolean coop = ArgumentParser.getBoolean(event.getOption("coop"), false);
        Hangman hangman = new Hangman(user, event.getChannel(), coop, lang);
        if (hangman.initialize()) {
            games.put(user, hangman);
        }
    }

    @Override
    public void handleEvent(Event event) {
        for (Hangman hangman : games.values()) {
            new Thread(() -> hangman.handleEvent(event)).start();
        }
    }

    public void removePlayer(String user) {
        games.remove(user);
    }

    public static HangmanCmd getInstance() {
        return instance;
    }
}
