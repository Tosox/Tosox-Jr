package de.tosoxdev.tosoxjr.games;

import de.tosoxdev.tosoxjr.games.hangman.Hangman;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final List<GameBase> games = new ArrayList<>();

    public GameManager() {
        addGame(new Hangman());
    }

    public List<GameBase> getGames() {
        return games;
    }

    public void addGame(GameBase cmd) throws IllegalArgumentException {
        boolean cmdExists = games.stream().anyMatch(i -> i.getName().equalsIgnoreCase(cmd.getName()));
        if (cmdExists) {
            throw new IllegalArgumentException("Found a duplicate item in the list");
        }
        games.add(cmd);
    }

    @NotNull
    public GameBase getGame(String name) {
        return games.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow();
    }

    public void handle(Event event) {
        getGames().forEach(g -> g.handle(event));
    }
}
