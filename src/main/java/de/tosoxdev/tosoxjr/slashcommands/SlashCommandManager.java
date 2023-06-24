package de.tosoxdev.tosoxjr.slashcommands;

import de.tosoxdev.tosoxjr.slashcommands.getbadge.GetBadgeCmd;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandManager {
    private final List<SlashCommandBase> commands = new ArrayList<>();

    public SlashCommandManager() {
        addCommand(new GetBadgeCmd());
    }

    public List<SlashCommandBase> getCommands() {
        return commands;
    }

    private void addCommand(SlashCommandBase cmd) {
        boolean commandName = commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(cmd.getName()));
        if (commandName) {
            throw new IllegalArgumentException("Found duplicate in the application command list");
        }
        commands.add(cmd);
    }

    public SlashCommandBase getCommand(String search) {
        return commands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(search))
                .findFirst()
                .orElse(null);
    }

    public void handle(SlashCommandInteractionEvent event) {
        String command = event.getName();
        SlashCommandBase cmd = getCommand(command);
        cmd.handle(event);
    }
}
