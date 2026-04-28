package de.tosox.tosoxjr.game;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class GameBase {
    private Timer timeoutTimer;

    public abstract boolean initialize();

    public void onMessageReactionAdd(MessageReactionAddEvent event) {}

    public void onMessageReceived(MessageReceivedEvent event) {}

    public abstract void end();

    protected void startTimeout(long timeoutMs, Runnable onTimeout) {
        timeoutTimer = new Timer(true);
        timeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimeout.run();
            }
        }, timeoutMs);
    }

    protected void resetTimeout(long timeoutMs, Runnable onTimeout) {
        if (timeoutTimer != null) timeoutTimer.cancel();
        startTimeout(timeoutMs, onTimeout);
    }

    protected void cancelTimeout() {
        if (timeoutTimer != null) timeoutTimer.cancel();
    }
}
