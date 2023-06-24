package de.tosoxdev.tosoxjr.commands.quote;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
        super("quote", "Get a random quote");

        StringBuilder sb = new StringBuilder("Available sources:\n");
        sources.forEach((key, value) -> sb.append(String.format("- %s\n", key)));
        quoteList = sb.toString();
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        String source = ArgumentParser.get(args, 0);
        if (source == null) {
            String msg = String.format("Syntax: %squote <source>\n%s", Constants.BOT_PREFIX, quoteList);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        Callable<String> callable = sources.get(source);
        if (callable == null) {
            String msg = String.format("There are no quotes for '%s'", source);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        String quote = Quote.getFromCallable(callable);
        if (quote == null) {
            Main.LOGGER.error("The callable didn't return a value when trying to run 'quote'");
            return;
        }

        event.getChannel().sendMessage(quote).queue();
    }
}
