package de.tosoxdev.minigames.listener;

import de.tosoxdev.minigames.commands.CommandManager;
import de.tosoxdev.minigames.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Listener extends ListenerAdapter {
    private final CommandManager commandManager = new CommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println(Constants.BOT_NAME + " is ready");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Check if msg is a valid
        if (!event.isFromGuild()) return;
        if (!event.getMessage().getContentDisplay().startsWith(Constants.BOT_PREFIX)) return;
        if (event.getAuthor().isBot()) return;
        if (event.isWebhookMessage()) return;

        commandManager.handle(event);
    }
}
