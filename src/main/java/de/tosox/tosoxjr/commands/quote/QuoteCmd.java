package de.tosox.tosoxjr.commands.quote;

import de.tosox.tosoxjr.commands.CommandBase;
import de.tosox.tosoxjr.utils.ArgumentParser;
import de.tosox.tosoxjr.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteCmd extends CommandBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteCmd.class);

    private final HashMap<String, Callable<String>> categories = new HashMap<>(Map.of(
            "breaking-bad", Quote::getBreakingBad,
            "famous", Quote::getFamous,
            "wisdom", Quote::getWisdom,
            "inspirational", Quote::getInspirational
    ));
    private final String categoriesList;

    public QuoteCmd() {
        super("quote", "Show a random quote", List.of(
                new OptionData(OptionType.STRING, "category", "List all available categories with 'list'", false)
        ));

        StringBuilder sb = new StringBuilder("Available categories");
        categories.forEach((key, value) -> sb.append(String.format("\n- %s", key)));
        categoriesList = sb.toString();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String category = ArgumentParser.getString(event.getOption("category"), null);
        if (category == null) {
            // Get random quote
            int randomIdx = ThreadLocalRandom.current().nextInt(categories.size());
            String randomCategory = (String) categories.keySet().toArray()[randomIdx];
            Callable<String> callable = categories.get(randomCategory);

            String quote = Utils.getFromCallable(callable);
            if (quote == null) {
                LOGGER.error("The callable didn't return a value when trying to run 'quote'");
                return;
            }

            event.reply(quote).queue();
            return;
        }

        if (category.equalsIgnoreCase("list")) {
            event.reply(categoriesList).queue();
            return;
        }

        Callable<String> callable = categories.get(category);
        if (callable == null) {
            String msg = String.format("There are no quotes for '%s'", category);
            event.reply(msg).queue();
            return;
        }

        String quote = Utils.getFromCallable(callable);
        if (quote == null) {
            LOGGER.error("The callable didn't return a value when trying to run 'quote'");
            return;
        }

        event.reply(quote).queue();
    }
}
