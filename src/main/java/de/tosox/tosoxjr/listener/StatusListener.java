package de.tosox.tosoxjr.listener;

import de.tosox.tosoxjr.command.CommandManager;
import de.tosox.tosoxjr.util.Constants;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class StatusListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusListener.class);

    private final CommandManager commandManager;

    public StatusListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", Constants.BOT_NAME);

        List<CommandData> commandData = commandManager.getCommands()
                .stream()
                .map(c -> {
                    SlashCommandData cmd = Commands.slash(c.getName(), c.getDescription());
                    if (!c.getOptions().isEmpty()) {
                        cmd.addOptions(c.getOptions());
                    }
                    return cmd;
                })
                .collect(Collectors.toList());
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }
}
