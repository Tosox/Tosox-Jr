package de.tosoxdev.minigames;

import de.tosoxdev.minigames.listener.Listener;
import de.tosoxdev.minigames.utils.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main {
    private static JDA jda;

    public static void main(String[] args) throws InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(Constants.BOT_TOKEN)
                .addEventListeners(new Listener())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("for your commands"));
        jda = builder.build().awaitReady();
    }

    public static JDA getInstance() {
        return jda;
    }
}
