package de.tosoxdev.tosoxjr.games;

import de.tosoxdev.tosoxjr.GenericManagerBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class GameManager extends GenericManagerBase<GameBase, Event> {
    public GameManager() {

    }

    public void handle(Event event) {
        if (event instanceof MessageReceivedEvent e) {

        } else if (event instanceof MessageReactionAddEvent e) {

        }
    }
}
