package de.tosox.tosoxjr.game.hangman;

import de.tosox.tosoxjr.game.GameBase;
import de.tosox.tosoxjr.game.GameManager;
import de.tosox.tosoxjr.service.HangmanService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class HangmanGame extends GameBase {
	private static final int MAX_ATTEMPTS = 7;
	private static final long TIMEOUT_MS = 2 * 60 * 1000;

	private final GameManager gameManager;
	private final HangmanService hangmanService;
	private final User owner;
	private final MessageChannel channel;
	private final boolean coop;
	private final String language;
	private final String word;
	private final AtomicBoolean ended = new AtomicBoolean(false);

	private final Set<Character> guessedLetters = new HashSet<>();
	private String wordDefinition;
	private int attempts;
	private Message gameMessage;

	public HangmanGame(GameManager gameManager, HangmanService hangmanService,
	                   User owner, MessageChannel channel, String language, boolean coop) {
		super();
		this.gameManager = gameManager;
		this.hangmanService = hangmanService;
		this.owner = owner;
		this.channel = channel;
		this.coop = coop;
		this.language = language.isBlank() ? "en" : language;
		this.word = hangmanService.getRandomWord(language)
				.map(w -> w.replace("Ä", "AE").replace("Ö", "OE").replace("Ü", "UE"))
				.orElse(null);
	}

	@Override
	public boolean initialize() {
		if (word == null) {
			return false;
		}

		channel.sendMessageEmbeds(createGameEmbed(HangmanStatus.ONGOING)).queue(message -> {
			gameMessage = message;
			startTimeoutTimer();
		});
		return true;
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (gameMessage == null || !event.getMessageId().equals(gameMessage.getId())) return;
		if (event.getEmoji().getType() == Emoji.Type.CUSTOM) return;

		User user = event.getUser();
		if ((user == null) || (user.isBot())) return;
		if (!coop && user.getIdLong() != owner.getIdLong()) return;

		resetTimeoutTimer();

		int codePoint = event.getEmoji().getName().codePointAt(0);
		switch (codePoint) {
			case 0x1F6D1 -> { if (user.getIdLong() == owner.getIdLong()) endGame(HangmanStatus.DEFEAT); }
			case 0x1F0CF -> handleHintRequest(user);
			default -> handleLetterGuess(codePoint);
		}
	}

	private void startTimeoutTimer() {
		startTimeout(TIMEOUT_MS, () -> endGame(HangmanStatus.TIMEOUT));
	}

	private void resetTimeoutTimer() {
		resetTimeout(TIMEOUT_MS, () -> endGame(HangmanStatus.TIMEOUT));
	}

	private void handleLetterGuess(int codePoint) {
		if (codePoint < 0x1F1E6 || codePoint > 0x1F1FF) return;

		char letter = (char) ('A' + (codePoint - 0x1F1E6));
		if (guessedLetters.contains(letter)) return;

		guessedLetters.add(letter);

		if (word.indexOf(letter) < 0) {
			attempts++;
			if (attempts >= MAX_ATTEMPTS) {
				endGame(HangmanStatus.DEFEAT);
				return;
			}
		}

		if (word.chars().allMatch(c -> guessedLetters.contains((char) c))) {
			endGame(HangmanStatus.WIN);
			return;
		}

		gameMessage.clearReactions().queue(v ->
				gameMessage.editMessageEmbeds(createGameEmbed(HangmanStatus.ONGOING)).queue()
		);
	}

	private void handleHintRequest(User user) {
		if (user.getIdLong() != owner.getIdLong()) return;
		if (wordDefinition != null) return;

		hangmanService.getDefinition(word).ifPresent(def -> {
			wordDefinition = def;
			gameMessage.clearReactions().queue(v ->
					gameMessage.editMessageEmbeds(createGameEmbed(HangmanStatus.ONGOING)).queue()
			);
		});
	}

	private String buildFigure() {
		return "```"
				+ "|‾‾‾‾‾‾‾‾‾‾‾‾|   \n|           "
				+ (attempts >= 1 ? "👑" : " ")
				+ "   \n|           "
				+ (attempts >= 2 ? (attempts >= 7 ? "😵" : "😨") : " ")
				+ "   \n|           "
				+ (attempts >= 3 ? "👘" : " ")
				+ "   \n|           "
				+ (attempts >= 4 ? "👖" : " ")
				+ "   \n|          "
				+ (attempts >= 5 ? (attempts >= 6 ? "👟👟" : "👟") : " ")
				+ "   \n|     \n|______________"
				+ "```";
	}

	private MessageEmbed createGameEmbed(HangmanStatus status) {
		StringBuilder wordProgress = new StringBuilder();
		for (char letter : word.toCharArray()) {
			if (status != HangmanStatus.ONGOING || guessedLetters.contains(letter)) {
				wordProgress.append(letter);
			} else {
				wordProgress.append('_');
			}
			wordProgress.append(' ');
		}

		String guessedDisplay = guessedLetters.stream()
				.sorted()
				.map(String::valueOf)
				.collect(Collectors.joining(", "));

		String title = String.format("%s[%s] %s",
				coop ? "[CO-OP] " : "",
				language.toUpperCase(),
				status.getTitle());

		EmbedBuilder embed = new EmbedBuilder()
				.setTitle(title)
				.setColor(status.getColor())
				.setDescription(buildFigure())
				.addField("Word", "```" + wordProgress.toString().trim() + "```", false)
				.addField("Attempts left", String.valueOf(MAX_ATTEMPTS - attempts), true)
				.setFooter("Request made by @" + owner.getName());

		if (!guessedDisplay.isEmpty()) {
			embed.addField("Guessed", guessedDisplay, true);
		}

		if (status == HangmanStatus.ONGOING) {
			if (wordDefinition != null) {
				embed.addField("Hint", wordDefinition, false);
			}
			embed.addField("How To Play", """
					- React with 🇦-🇿 to guess a letter
					- React with 🃏 to reveal a hint (owner only)
					- React with 🛑 to end the game
					""", false);
		}

		return embed.build();
	}

	private void endGame(HangmanStatus status) {
		if (!ended.compareAndSet(false, true)) return;
		cancelTimeout();
		gameMessage.clearReactions().queue(v ->
				gameMessage.editMessageEmbeds(createGameEmbed(status)).queue()
		);
		end();
	}

	@Override
	public void end() {
		gameManager.unregisterGame(owner);
	}
}
