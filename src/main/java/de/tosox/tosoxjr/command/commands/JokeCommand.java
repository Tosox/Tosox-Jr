package de.tosox.tosoxjr.command.commands;

import de.tosox.tosoxjr.command.CommandBase;
import de.tosox.tosoxjr.service.JokeService;
import de.tosox.tosoxjr.util.ArgumentParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class JokeCommand extends CommandBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(JokeCommand.class);

	private final Map<String, Supplier<Optional<String>>> categories;
    private final String categoriesList;

    public JokeCommand(JokeService jokeService) {
        super("joke", "Tell a random joke", List.of(
                new OptionData(OptionType.STRING, "category", "List all available categories with 'list'", false)
        ));

	    this.categories = Map.of(
                "pun", jokeService::getPun,
                "programming", jokeService::getProgramming,
                "chuck-norris", jokeService::getChuckNorris
        );

        StringBuilder sb = new StringBuilder("Available categories");
        categories.keySet().forEach(category -> sb.append("\n- ").append(category));
        categoriesList = sb.toString();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String category = ArgumentParser.getString(event.getOption("category"), null);

        if (category == null) {
            String randomCategory = getRandomCategory();
            sendJoke(event, randomCategory);
        } else if (category.equalsIgnoreCase("list")) {
            event.reply(categoriesList).queue();
        } else if (categories.containsKey(category)) {
            sendJoke(event, category);
        } else {
            event.reply("❌ Unknown category: `" + category + "`").setEphemeral(true).queue();
        }
    }

    private String getRandomCategory() {
        List<String> keys = new ArrayList<>(categories.keySet());
        return keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
    }

    private void sendJoke(SlashCommandInteractionEvent event, String category) {
        Supplier<Optional<String>> jokeSupplier = categories.get(category);
        jokeSupplier.get().ifPresentOrElse(
                joke -> event.reply(joke).queue(),
                () -> {
                    LOGGER.error("No joke found for category '{}'", category);
                    event.reply("😅 Sorry, I couldn’t fetch a joke from `" + category + "` right now.").queue();
                }
        );
    }
}
