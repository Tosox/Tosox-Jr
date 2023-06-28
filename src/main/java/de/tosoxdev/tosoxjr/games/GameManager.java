package de.tosoxdev.tosoxjr.games;

import de.tosoxdev.tosoxjr.GenericManagerBase;
import de.tosoxdev.tosoxjr.games.hangman.Hangman;
import net.dv8tion.jda.api.events.Event;

public class GameManager extends GenericManagerBase<GameBase, Event> {
    public GameManager() {
        addElement(new Hangman());
    }

    public void handle(Event event) {
        getElements().forEach(g -> g.handle(event));
    }
}
