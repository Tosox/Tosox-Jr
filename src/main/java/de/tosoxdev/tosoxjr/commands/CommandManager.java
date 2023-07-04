package de.tosoxdev.tosoxjr.commands;

import de.tosoxdev.tosoxjr.commands.cat.CatCmd;
import de.tosoxdev.tosoxjr.commands.csstats.CSStatsCmd;
import de.tosoxdev.tosoxjr.commands.hangman.HangmanCmd;
import de.tosoxdev.tosoxjr.commands.quote.QuoteCmd;
import de.tosoxdev.tosoxjr.commands.say.SayCmd;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private final List<CommandBase> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new SayCmd());
        addCommand(new CatCmd());
        addCommand(new QuoteCmd());
        addCommand(new CSStatsCmd());
        addCommand(new HangmanCmd());
    }

    public List<CommandBase> getCommands() {
        return commands;
    }

    public void addCommand(CommandBase cmd) throws IllegalArgumentException {
        boolean cmdExists = commands.stream().anyMatch(i -> i.getName().equalsIgnoreCase(cmd.getName()));
        if (cmdExists) {
            throw new IllegalArgumentException("Found a duplicate item in the list");
        }
        commands.add(cmd);
    }

    @NotNull
    public CommandBase getCommand(String name) {
        return commands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow();
    }

    public void handle(SlashCommandInteractionEvent event) {
        String command = event.getName();
        CommandBase cmd = getCommand(command);
        cmd.handle(event);
    }
}
