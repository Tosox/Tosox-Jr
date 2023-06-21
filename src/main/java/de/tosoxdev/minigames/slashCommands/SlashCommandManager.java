package de.tosoxdev.minigames.slashCommands;

import de.tosoxdev.minigames.slashCommands.badge.GetBadgeCmd;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandManager {
    private final List<ISlashCommand> commands = new ArrayList<>();

    public SlashCommandManager() {
        addCommand(new GetBadgeCmd());
    }

    public List<ISlashCommand> getCommands() {
        return commands;
    }

    private void addCommand(ISlashCommand cmd) {
        boolean commandName = commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(cmd.getName()));
        if (commandName) {
            throw new IllegalArgumentException("Command is already in the list");
        }
        commands.add(cmd);
    }

    public ISlashCommand getCommand(String search) {
        return this.commands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(search))
                .findFirst()
                .orElse(null);
    }

    public void handle(SlashCommandInteractionEvent event) {
        String command = event.getName();
        ISlashCommand cmd = getCommand(command);
        cmd.handle(new SlashCommandContext(event));
    }
}