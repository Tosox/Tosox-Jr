package de.tosoxdev.tosoxjr.listener;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class UserInputListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Check if message is a valid
        if (!event.isFromGuild()) return;
        if (!event.getMessage().getContentDisplay().startsWith(Constants.BOT_PREFIX)) return;
        if (event.getAuthor().isBot()) return;
        if (event.isWebhookMessage()) return;

        Main.getCommandManager().handle(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Main.getSlashCommandManager().handle(event);
    }
}
