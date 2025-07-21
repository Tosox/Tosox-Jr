package de.tosoxdev.tosoxjr;

import de.tosoxdev.tosoxjr.commands.CommandManager;
import de.tosoxdev.tosoxjr.listener.StatusListener;
import de.tosoxdev.tosoxjr.listener.UserInputListener;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    ///////////////////////////////////////////////////////////////
    // Minimal Permissions:
    //   Scopes:
    //     - bot
    //     - applications.commands
    //   Permissions:
    //     - Send Messages
    //     - Manage Messages
    //
    // Prod: https://discord.com/api/oauth2/authorize?client_id=853752473365250089&permissions=10240&scope=bot%20applications.commands
    // Dev : https://discord.com/api/oauth2/authorize?client_id=1125333842186752091&permissions=10240&scope=bot%20applications.commands
    ///////////////////////////////////////////////////////////////

    private static final CommandManager COMMAND_MANAGER = new CommandManager();

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

    public static CommandManager getCommandManager() {
        return COMMAND_MANAGER;
    }
}
