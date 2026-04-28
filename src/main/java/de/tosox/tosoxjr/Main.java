package de.tosox.tosoxjr;

import de.tosox.tosoxjr.command.CommandManager;
import de.tosox.tosoxjr.game.GameManager;
import de.tosox.tosoxjr.listener.MessageListener;
import de.tosox.tosoxjr.listener.ReactionListener;
import de.tosox.tosoxjr.listener.StatusListener;
import de.tosox.tosoxjr.listener.SlashCommandListener;
import de.tosox.tosoxjr.util.Constants;
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

    public static void main(String[] args) throws InterruptedException {
        GameManager gameManager = new GameManager();
        CommandManager commandManager = new CommandManager(gameManager);

        JDABuilder.createDefault(Constants.BOT_TOKEN)
                .addEventListeners(
                        new StatusListener(commandManager),
                        new SlashCommandListener(commandManager),
                        new MessageListener(gameManager),
                        new ReactionListener(gameManager)
                )
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("for your commands"))
                .setStatus(OnlineStatus.ONLINE)
                .build()
                .awaitReady();
    }
}
