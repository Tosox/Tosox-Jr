package de.tosoxdev.tosoxjr.listener;

import de.tosoxdev.tosoxjr.commands.CommandManager;
import de.tosoxdev.tosoxjr.slashcommands.SlashCommandManager;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class MessageListener extends ListenerAdapter {
    private final CommandManager commandManager = new CommandManager();
    private final SlashCommandManager slashCommandManager = new SlashCommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println(Constants.BOT_NAME + " is ready");

        // Global application commands
        List<CommandData> commandData = slashCommandManager.getCommands()
                .stream()
                .map(c -> Commands.slash(c.getName(), c.getDescription()))
                .collect(Collectors.toList());
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Check if message is a valid
        if (!event.isFromGuild()) return;
        if (!event.getMessage().getContentDisplay().startsWith(Constants.BOT_PREFIX)) return;
        if (event.getAuthor().isBot()) return;
        if (event.isWebhookMessage()) return;

        commandManager.handle(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        slashCommandManager.handle(event);
    }
}
