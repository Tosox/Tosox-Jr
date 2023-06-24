package de.tosoxdev.tosoxjr.listener;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class StatusListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Main.getLogger().info("{} is ready", Constants.BOT_NAME);

        // Global application commands
        List<CommandData> commandData = Main.getSlashCommandManager().getCommands()
                .stream()
                .map(c -> Commands.slash(c.getName(), c.getDescription()))
                .collect(Collectors.toList());
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }
}
