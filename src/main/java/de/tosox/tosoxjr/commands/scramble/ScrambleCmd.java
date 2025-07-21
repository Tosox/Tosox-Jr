package de.tosox.tosoxjr.commands.scramble;

import de.tosox.tosoxjr.commands.GameBase;
import de.tosox.tosoxjr.commands.hangman.Hangman;
import de.tosox.tosoxjr.utils.ArgumentParser;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.List;

public class ScrambleCmd extends GameBase {
    private static final int MAX_GAMES = 10;
    private static ScrambleCmd instance;
    private final HashMap<String, Scramble> games = new HashMap<>();
    private final String languages;

    public ScrambleCmd() {
        super("scramble", "Play a game of Scramble", List.of(
                new OptionData(OptionType.STRING, "lang", "Decide the langauge of the word. Use 'list' to list all available ones", false),
                new OptionData(OptionType.BOOLEAN, "coop", "Play Scramble with all your friends on the server", false)
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
            event.reply("You already started a game of Scramble").queue();
            return;
        }

        if (games.size() > MAX_GAMES) {
            event.reply("Sorry, there are to many games of Scramble already").queue();
            return;
        }

        event.deferReply().queue(m -> m.deleteOriginal().queue());

        boolean coop = ArgumentParser.getBoolean(event.getOption("coop"), false);
        Scramble scramble = new Scramble(user, event.getChannel(), coop, lang);
        if (scramble.initialize()) {
            games.put(user, scramble);
        }
    }

    @Override
    public void handleEvent(Event event) {
        for (Scramble scramble : games.values()) {
            new Thread(() -> scramble.handleEvent(event)).start();
        }
    }

    public void removePlayer(String user) {
        games.remove(user);
    }

    public static ScrambleCmd getInstance() {
        return instance;
    }
}
