package de.tosoxdev.tosoxjr.games;

import de.tosoxdev.tosoxjr.GenericCommandBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class GameBase extends GenericCommandBase<Event> {
    public GameBase(String name, String description) {
        super(name, description);
    }

    public abstract void run(MessageReceivedEvent event);
}
