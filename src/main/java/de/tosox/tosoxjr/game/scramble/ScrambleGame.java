package de.tosox.tosoxjr.game.scramble;

import de.tosox.tosoxjr.game.GameBase;
import de.tosox.tosoxjr.game.GameManager;
import de.tosox.tosoxjr.service.ScrambleService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScrambleGame extends GameBase {
	private static final long TIMEOUT_MS = 10 * 60 * 1000;
	private static final int STOP_SIGN_CP = 0x1F6D1;

	private final GameManager gameManager;
	private final ScrambleService scrambleService;
	private final User owner;
	private final MessageChannel channel;
	private final boolean coop;
	private final String language;

	private final AtomicBoolean ended = new AtomicBoolean(false);
	private String embedMessageId;
	private long startTime;
	private String word;
	private String scrambledWord;

	public ScrambleGame(GameManager gameManager, ScrambleService scrambleService,
	                    User owner, MessageChannel channel, boolean coop, String language) {
		this.gameManager = gameManager;
		this.scrambleService = scrambleService;
		this.owner = owner;
		this.channel = channel;
		this.coop = coop;
		this.language = language;
	}

	@Override
	public boolean initialize() {
		word = scrambleService.getRandomWord(language).orElse(null);
		if (word == null) {
			channel.sendMessage("I'm unable to generate a random word").queue();
			return false;
		}

		// Bugfix for words with spaces
		word = word.replaceAll("\\s+", "");

		List<String> chars = new ArrayList<>(word
				.toLowerCase()
				.chars()
				.mapToObj(c -> String.valueOf((char) c))
				.toList());
		Collections.shuffle(chars);
		scrambledWord = String.join("", chars);

		startTime = System.currentTimeMillis();
		channel.sendMessageEmbeds(createGameEmbed(ScrambleStatus.ONGOING, null)).queue(m -> {
			embedMessageId = m.getId();
			m.addReaction(Emoji.fromUnicode(new String(Character.toChars(STOP_SIGN_CP)))).queue();
			startTimeout(TIMEOUT_MS, () -> endGame(ScrambleStatus.TIMEOUT, null));
		});
		return true;
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (embedMessageId == null || !event.getMessageId().equals(embedMessageId)) return;

		EmojiUnion emoji = event.getEmoji();
		if (emoji.getType() == Emoji.Type.CUSTOM) return;

		User sender = event.getUser();
		if (sender == null || sender.isBot()) return;
		if (sender.getIdLong() != owner.getIdLong()) return;

		int codePoint = emoji.getName().codePointAt(0);
		if (codePoint != STOP_SIGN_CP) return;

		endGame(ScrambleStatus.DEFEAT, null);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isWebhookMessage()) return;
		if (!event.getChannel().getId().equals(channel.getId())) return;

		User sender = event.getAuthor();
		if (sender.isBot()) return;
		if (!coop && sender.getIdLong() != owner.getIdLong()) return;

		resetTimeout(TIMEOUT_MS, () -> endGame(ScrambleStatus.TIMEOUT, null));

		if (event.getMessage().getContentDisplay().equalsIgnoreCase(word)) {
			endGame(ScrambleStatus.WIN, sender.getName());
		}
	}

	private void endGame(ScrambleStatus status, String sender) {
		if (!ended.compareAndSet(false, true)) return;
		cancelTimeout();
		if (embedMessageId != null) {
			channel.retrieveMessageById(embedMessageId).queue(m -> m.clearReactions().queue());
		}
		channel.sendMessageEmbeds(createGameEmbed(status, sender)).queue();
		end();
	}

	@Override
	public void end() {
		gameManager.unregisterGame(owner);
	}

	private MessageEmbed createGameEmbed(ScrambleStatus status, String sender) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(String.format("%s[%s] %s",
				coop ? "[CO-OP] " : "",
				language.isBlank() ? "EN" : language.toUpperCase(),
				status.getTitle()));
		embed.setColor(status.getColor());
		embed.addField(status == ScrambleStatus.ONGOING ? "Word" : "The word was",
				status == ScrambleStatus.ONGOING ? scrambledWord : word, false);

		if (status == ScrambleStatus.WIN) {
			double time = (double) (System.currentTimeMillis() - startTime) / 1000;
			String results = coop
					? String.format("%s guessed the word first after %.2fs", sender, time)
					: String.format("You guessed the word after %.2fs", time);
			embed.addField("Results", results, false);
		}

		if (status == ScrambleStatus.ONGOING) {
			embed.addField("How To Play", """
					- Try to unscramble the word
					- Write your guess in this channel
					- React with the stop sign (🛑) to end the game
					""", false);
		}

		embed.setFooter("Request made by @" + owner.getName());
		return embed.build();
	}
}
