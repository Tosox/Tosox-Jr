package de.tosox.tosoxjr.listener;

import de.tosox.tosoxjr.game.GameBase;
import de.tosox.tosoxjr.game.GameManager;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReactionListener extends ListenerAdapter {
	private final GameManager gameManager;

	public ReactionListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
		if (event.getUser() == null) return;

		// Route to the owner's game directly (non-coop fast path)
		GameBase ownerGame = gameManager.getGame(event.getUser());
		if (ownerGame != null) {
			ownerGame.onMessageReactionAdd(event);
		}

		// Broadcast to all other games for co-op (mirrors old handleEvent dispatch)
		for (GameBase game : gameManager.getAllGames()) {
			if (game != ownerGame) {
				game.onMessageReactionAdd(event);
			}
		}
	}
}
