package de.tosoxdev.tosoxjr.commands.quote;

import de.tosoxdev.tosoxjr.commands.ICommand;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class QuoteCmd implements ICommand {
    private final HashMap<String, Callable<String>> sources = new HashMap<>(Map.of(
            "breaking-bad", Quotes::getBreakingBad,
            "joke", Quotes::getJoke,
            "famous", Quotes::getFamous,
            "wisdom", Quotes::getWisdom,
            "inspirational", Quotes::getInspirational
    ));

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        String source = ArgumentParser.get(args, 0);
        if (source == null) {
            String msg = String.format("Please use the correct syntax: %squote <source>", Constants.BOT_PREFIX);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        if (source.equalsIgnoreCase("list")) {
            printList(event);
            return;
        }

        Callable<String> callable = sources.get(source);
        if (callable == null) {
            String msg = String.format("There are no quotes for '%s'. Try '%squote list' to list all sources.", source, Constants.BOT_PREFIX);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        String quote = Quotes.getFromCallable(callable);
        if (quote == null) {
            System.out.println("[ERROR]: The callable didn't return a value when trying to run 'quote'");
            return;
        }

        event.getChannel().sendMessage(quote).queue();
    }

    @Override
    public String getName() {
        return "quote";
    }

    @Override
    public String getHelp() {
        return "Get a random quote";
    }

    private void printList(MessageReceivedEvent event) {
        StringBuilder sb = new StringBuilder("Available sources:\n");
        sources.forEach((key, value) -> sb.append(String.format("- %s\n", key)));
        event.getChannel().sendMessage(sb).queue();
    }
}
