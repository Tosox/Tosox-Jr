package de.tosoxdev.tosoxjr.commands.joke;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import de.tosoxdev.tosoxjr.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class JokeCmd extends CommandBase {
    private final HashMap<String, Callable<String>> categories = new HashMap<>(Map.of(
            "general", Joke::getGeneral,
            "programming", Joke::getProgramming,
            "chuck-norris", Joke::getChuckNorris
    ));
    private final String categoriesList;

    public JokeCmd() {
        super("joke", "Show a random joke", List.of(
                new OptionData(OptionType.STRING, "category", "List all available categories with 'list'", false)
        ));

        StringBuilder sb = new StringBuilder();
        categories.forEach((key, value) -> sb.append(String.format("- %s\n", key)));
        categoriesList = sb.toString();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String category = ArgumentParser.getString(event.getOption("category"), null);
        if (category == null) {
            // Get random joke
            int randomIdx = ThreadLocalRandom.current().nextInt(categories.size());
            String randomCategory = (String) categories.keySet().toArray()[randomIdx];
            Callable<String> callable = categories.get(randomCategory);

            String joke = Utils.getStringFromCallable(callable);
            if (joke == null) {
                Main.getLogger().error("The callable didn't return a value when trying to run 'joke'");
                return;
            }

            event.reply(joke).queue();
            return;
        }

        if (category.equalsIgnoreCase("list")) {
            String msg = String.format("Available categories\n%s", categoriesList);
            event.reply(msg).queue();
            return;
        }

        Callable<String> callable = categories.get(category);
        if (callable == null) {
            String msg = String.format("There are no jokes for '%s'", category);
            event.reply(msg).queue();
            return;
        }

        String joke = Utils.getStringFromCallable(callable);
        if (joke == null) {
            Main.getLogger().error("The callable didn't return a value when trying to run 'joke'");
            return;
        }

        event.reply(joke).queue();
    }
}
