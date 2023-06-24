package de.tosoxdev.tosoxjr;

import de.tosoxdev.tosoxjr.listener.MessageListener;
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
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        JDABuilder.createDefault(Constants.BOT_TOKEN)
                .addEventListeners(new MessageListener())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("for your commands"))
                .setStatus(OnlineStatus.ONLINE)
                .build()
                .awaitReady();
    }
}
