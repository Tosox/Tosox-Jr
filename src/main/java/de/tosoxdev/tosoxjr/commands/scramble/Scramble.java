package de.tosoxdev.tosoxjr.commands.scramble;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Scramble {
    public static final HashMap<String, String> RANDOM_WORD_APIS = new HashMap<>(Map.of(
            "en", "https://random-word-api.vercel.app/api?words=1",
            "de", "https://alex-riedel.de/randV2.php?anz=1"
    ));
    private static final int TIMEOUT_MS = 2 * 60 * 1000;
    private static final int STOP_SIGN_CP = 0x1F6D1;

    private final MessageChannel channel;
    private final String player;
    private final boolean coop;
    private final String language;

    private Timer tmTimeout = new Timer();
    private String embedMessageId;
    private long timer;
    private int attempts;
    private String word;
    private String scrambledWord;

    public Scramble(String player, MessageChannel channel, boolean coop, String language) {
        this.channel = channel;
        this.player = player;
        this.coop = coop;
        this.language = language;
    }

    public boolean initialize() {
        word = generateWord();
        if (word == null) {
            channel.sendMessage("I'm unable to generate a random word").queue();
            return false;
        }

        // Shuffle word
        List<String> chars = new ArrayList<>(word.toUpperCase().chars().mapToObj(c -> String.valueOf((char) c)).toList());
        Collections.shuffle(chars);
        scrambledWord = String.join("", chars);

        // Start timers
        tmTimeout.schedule(new TimerTask() {
            @Override
            public void run() {
                endGame(GameState.TIMEOUT, null);
            }
        }, TIMEOUT_MS);
        timer = System.currentTimeMillis();

        channel.sendMessageEmbeds(createGameEmbed(GameState.ONGOING, null).build()).queue(m -> embedMessageId = m.getId());
        return true;
    }

    public void handleEvent(Event event) {
        if (event instanceof MessageReceivedEvent e) {
            handleMessageReceivedEvent(e);
        } else if (event instanceof MessageReactionAddEvent e) {
            handleMessageReactionAddEvent(e);
        }
    }

    private void handleMessageReactionAddEvent(MessageReactionAddEvent event) {
        EmojiUnion emoji = event.getEmoji();
        if (!event.getMessageId().equals(embedMessageId)) return;
        if (emoji.getType() == Emoji.Type.CUSTOM) return;

        // Check sender
        User sender = event.getUser();
        if (sender == null) return;
        if (sender.isBot()) return;
        if (!sender.getAsTag().equals(player)) return;

        // Get code point from emoji
        int codePoint = emoji.getName().codePointAt(0);
        if (codePoint != STOP_SIGN_CP) return;

        endGame(GameState.DEFEAT, null);
    }

    private void handleMessageReceivedEvent(MessageReceivedEvent event) {
        if (event.isWebhookMessage()) return;
        if (!event.getChannel().getId().equals(channel.getId())) return;

        User sender = event.getAuthor();
        if (sender.isBot()) return;
        if ((!coop) && (!sender.getAsTag().equals(player))) return;

        // Reset timeout timer
        resetTimer();

        attempts++;

        if (event.getMessage().getContentDisplay().equalsIgnoreCase(word)) {
            endGame(GameState.WIN, sender.getName());
        }
    }

    private void resetTimer() {
        tmTimeout.cancel();
        tmTimeout = new Timer();
        tmTimeout.schedule(new TimerTask() {
            @Override
            public void run() {
                endGame(GameState.TIMEOUT, null);
            }
        }, TIMEOUT_MS);
    }

    private void endGame(GameState state, String sender) {
        tmTimeout.cancel();
        channel.retrieveMessageById(embedMessageId).queue(m -> m.clearReactions().queue());
        channel.sendMessageEmbeds(createGameEmbed(state, sender).build()).queue();
        ScrambleCmd.getInstance().removePlayer(player);
    }

    private String generateWord() {
        String lang = RANDOM_WORD_APIS.getOrDefault(language.toLowerCase(), RANDOM_WORD_APIS.get("en"));
        String response = APIRequest.getString(lang);
        if (response == null) {
            return null;
        }

        // Remove brackets and quotation marks
        response = response.substring(2, response.length() - 2);

        // Replace german umlauts
        response = response.replace("&auml;", "ä").replace("&ouml;", "ö").replace("&uuml;", "ü").replace("&szlig;", "ß");

        return response;
    }

    private EmbedBuilder createGameEmbed(GameState state, String sender) {
        EmbedBuilder gameEmbed = new EmbedBuilder();
        gameEmbed.setTitle(state.getTitle());
        gameEmbed.setColor(Color.CYAN);
        gameEmbed.addField(state == GameState.ONGOING ? "Word" : "The word was", state == GameState.ONGOING ? scrambledWord : word, false);
        if (state == GameState.WIN) {
            double time = (double)(System.currentTimeMillis() - timer) / 1000;
            String results = coop
                    ? String.format("%s guessed the word first after %.2fs", sender, time)
                    : String.format("You guessed the word after %.2fs and %d attempts", time, attempts);
            gameEmbed.addField("Results", results, false);
        }
        if (state == GameState.ONGOING) {
            gameEmbed.addField(
                    "How To Play",
                    """
                    - Try to unscramble the word
                    - Write your guess in this channel
                    - React with the stop sign (🛑) to end the game
                    """, false);
        }
        return gameEmbed;
    }
}
