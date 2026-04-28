package de.tosox.tosoxjr.command;

import de.tosox.tosoxjr.command.commands.*;
import de.tosox.tosoxjr.game.GameManager;
import de.tosox.tosoxjr.service.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CommandManager {
    private final List<CommandBase> commands = new ArrayList<>();

    public CommandManager(GameManager gameManager) {
        CatService catService = new CatService();
        CSStatsService csStatsService = new CSStatsService();
        JokeService jokeService = new JokeService();
        QuoteService quoteService = new QuoteService();
        HangmanService hangmanService = new HangmanService();
        ScrambleService scrambleService = new ScrambleService();

        registerCommand(new CatCommand(catService));
        registerCommand(new CSStatsCommand(csStatsService));
        registerCommand(new JokeCommand(jokeService));
        registerCommand(new QuoteCommand(quoteService));
        registerCommand(new SayCommand());

        registerCommand(new HangmanCommand(gameManager, hangmanService));
        registerCommand(new ScrambleCommand(gameManager, scrambleService));
    }

    public List<CommandBase> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public void registerCommand(CommandBase cmd) throws IllegalArgumentException {
        boolean exists = commands.stream()
                .anyMatch(existing -> existing.getName().equalsIgnoreCase(cmd.getName()));
        if (exists) {
            throw new IllegalArgumentException("Duplicate command: " + cmd.getName());
        }
        commands.add(cmd);
    }

    public Optional<CommandBase> getCommand(String name) {
        return commands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public void handleCommand(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        getCommand(commandName).ifPresentOrElse(
                cmd -> cmd.handle(event),
                () -> event.reply("Unknown command: `" + commandName + "`").setEphemeral(true).queue()
        );
    }
}
