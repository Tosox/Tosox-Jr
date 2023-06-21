package de.tosoxdev.minigames;

import de.tosoxdev.minigames.listener.MessageListener;
import de.tosoxdev.minigames.utils.Constants;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
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
