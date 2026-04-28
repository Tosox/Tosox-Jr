package de.tosox.tosoxjr.command.commands;

import de.tosox.tosoxjr.command.CommandBase;
import de.tosox.tosoxjr.service.QuoteService;
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

public class QuoteCommand extends CommandBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteCommand.class);

    private final Map<String, Supplier<Optional<String>>> categories;
    private final String categoriesList;

    public QuoteCommand(QuoteService quoteService) {
        super("quote", "Show a random quote", List.of(
                new OptionData(OptionType.STRING, "category", "List all available categories with 'list'", false)
        ));

        this.categories = Map.of(
                "breaking-bad", quoteService::getBreakingBad,
                "famous", quoteService::getFamous,
                "inspirational", quoteService::getInspirational
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
            sendQuote(event, randomCategory);
        } else if (category.equalsIgnoreCase("list")) {
            event.reply(categoriesList).queue();
        } else if (categories.containsKey(category)) {
            sendQuote(event, category);
        } else {
            event.reply("❌ Unknown category: `" + category + "`").setEphemeral(true).queue();
        }
    }

    private String getRandomCategory() {
        List<String> keys = new ArrayList<>(categories.keySet());
        return keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
    }

    private void sendQuote(SlashCommandInteractionEvent event, String category) {
        Supplier<Optional<String>> quoteSupplier = categories.get(category);
        quoteSupplier.get().ifPresentOrElse(
                quote -> event.reply(quote).queue(),
                () -> {
                    LOGGER.error("No quote found for category '{}'", category);
                    event.reply("😅 Sorry, I couldn’t fetch a quote from `" + category + "` right now.").queue();
                }
        );
    }
}
