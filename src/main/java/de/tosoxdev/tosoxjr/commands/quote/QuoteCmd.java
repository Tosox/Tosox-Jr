package de.tosoxdev.tosoxjr.commands.quote;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class QuoteCmd extends CommandBase {
    private final HashMap<String, Callable<String>> sources = new HashMap<>(Map.of(
            "breaking-bad", Quote::getBreakingBad,
            "joke", Quote::getJoke,
            "famous", Quote::getFamous,
            "wisdom", Quote::getWisdom,
            "inspirational", Quote::getInspirational
    ));
    private final String quoteList;

    public QuoteCmd() {
        super("quote", "Show a random quote", List.of(
                new OptionData(OptionType.STRING, "category", "List all available categories with '/quote list'", true)
        ));

        StringBuilder sb = new StringBuilder();
        sources.forEach((key, value) -> sb.append(String.format("- %s\n", key)));
        quoteList = sb.toString();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String category = ArgumentParser.getStringForced(event.getOption("category"));
        if (category.equalsIgnoreCase("list")) {
            String msg = String.format("Available categories\n%s", quoteList);
            event.reply(msg).queue();
            return;
        }

        Callable<String> callable = sources.get(category);
        if (callable == null) {
            String msg = String.format("There are no quotes for '%s'. Try running /quote list", category);
            event.reply(msg).queue();
            return;
        }

        String quote = Quote.getFromCallable(callable);
        if (quote == null) {
            Main.getLogger().error("The callable didn't return a value when trying to run 'quote'");
            return;
        }

        event.reply(quote).queue();
    }
}
