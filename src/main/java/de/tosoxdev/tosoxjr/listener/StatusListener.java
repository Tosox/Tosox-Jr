package de.tosoxdev.tosoxjr.listener;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.utils.Constants;
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

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", Constants.BOT_NAME);

        // Global application commands
        List<CommandData> commandData = Main.getCommandManager().getCommands()
                .stream()
                .map(c -> {
                    SlashCommandData cmd = Commands.slash(c.getName(), c.getDescription());
                    if ((c.getOptions() != null) && (!c.getOptions().isEmpty())) {
                        cmd.addOptions(c.getOptions());
                    }
                    return cmd;
                })
                .collect(Collectors.toList());
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }
}
