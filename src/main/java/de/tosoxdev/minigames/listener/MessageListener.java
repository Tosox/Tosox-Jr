package de.tosoxdev.minigames.listener;

import de.tosoxdev.minigames.commands.CommandManager;
import de.tosoxdev.minigames.slashcommands.ISlashCommand;
import de.tosoxdev.minigames.slashcommands.SlashCommandManager;
import de.tosoxdev.minigames.utils.Constants;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageListener extends ListenerAdapter {
    private final CommandManager commandManager = new CommandManager();
    private final SlashCommandManager slashCommandManager = new SlashCommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println(Constants.BOT_NAME + " is ready");
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

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        for (ISlashCommand cmd : slashCommandManager.getCommands()) {
            commandData.add(Commands.slash(cmd.getName(), cmd.getHelp()));
        }
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
