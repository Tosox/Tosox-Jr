package de.tosox.tosoxjr.game;

import net.dv8tion.jda.api.entities.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
	private static final int MAX_GAMES_GLOBAL = 100;
	private final Map<Long, GameBase> activeGames = new ConcurrentHashMap<>();

	public boolean canStartNewGame() {
		return activeGames.size() < MAX_GAMES_GLOBAL;
	}

	public void registerGame(User owner, GameBase game) {
		activeGames.put(owner.getIdLong(), game);
	}

	public void unregisterGame(User owner) {
		activeGames.remove(owner.getIdLong());
	}

	public GameBase getGame(User user) {
		return activeGames.get(user.getIdLong());
	}

	public boolean hasGame(User user) {
		return activeGames.containsKey(user.getIdLong());
	}

	public long getActiveGameCount(Class<? extends GameBase> type) {
		return activeGames.values().stream()
				.filter(type::isInstance)
				.count();
	}

	public Collection<GameBase> getAllGames() {
		return activeGames.values();
	}
}
