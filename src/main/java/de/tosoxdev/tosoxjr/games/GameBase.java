package de.tosoxdev.tosoxjr.games;

import de.tosoxdev.tosoxjr.GenericCommandBase;
import net.dv8tion.jda.api.events.Event;

public abstract class GameBase extends GenericCommandBase<Event> {
    public GameBase(String name, String description) {
        super(name, description);
    }
}
