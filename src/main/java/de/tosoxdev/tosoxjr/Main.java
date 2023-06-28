package de.tosoxdev.tosoxjr;

import de.tosoxdev.tosoxjr.commands.CommandManager;
import de.tosoxdev.tosoxjr.listener.StatusListener;
import de.tosoxdev.tosoxjr.listener.UserInputListener;
import de.tosoxdev.tosoxjr.slashcommands.SlashCommandManager;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    // Share bot permissions:
    //   Scopes:
    //     - bot
    //     - applications.commands
    //   Permissions:
    //     - Send Messages
    //     - Manage Messages
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final CommandManager COMMAND_MANAGER = new CommandManager();
    private static final SlashCommandManager SLASH_COMMAND_MANAGER = new SlashCommandManager();

    public static void main(String[] args) throws InterruptedException {
        JDABuilder.createDefault(Constants.BOT_TOKEN)
                .addEventListeners(new StatusListener())
                .addEventListeners(new UserInputListener())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("for your commands"))
                .setStatus(OnlineStatus.ONLINE)
                .build()
                .awaitReady();
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static CommandManager getCommandManager() {
        return COMMAND_MANAGER;
    }

    public static SlashCommandManager getSlashCommandManager() {
        return SLASH_COMMAND_MANAGER;
    }
}
