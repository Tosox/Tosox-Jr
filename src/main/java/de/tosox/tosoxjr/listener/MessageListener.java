package de.tosox.tosoxjr.listener;

import de.tosox.tosoxjr.game.GameBase;
import de.tosox.tosoxjr.game.GameManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {
	private final GameManager gameManager;

	public MessageListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		// Route to the owner's game directly (non-coop fast path)
		GameBase ownerGame = gameManager.getGame(event.getAuthor());
		if (ownerGame != null) {
			ownerGame.onMessageReceived(event);
		}

		// Broadcast to all other games for co-op (mirrors old handleEvent dispatch)
		for (GameBase game : gameManager.getAllGames()) {
			if (game != ownerGame) {
				game.onMessageReceived(event);
			}
		}
	}
}
