package de.tosoxdev.tosoxjr.commands.hangman;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

enum GameState {
    ONGOING("Hangman"),
    WIN("You won!"),
    DEFEAT("You lost!"),
    TIMEOUT("Timeout");

    private final String title;

    GameState(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

public class Hangman {
    private static final String API_RANDOM_WORD = "https://random-word-api.vercel.app/api?words=1";
    private static final String API_DICTIONARY = "https://www.dictionaryapi.com/api/v3/references/collegiate/json/%s?key=%s";
    private static final int REGIONAL_INDICATOR_A_CP = 0x1F1E6;
    private static final int REGIONAL_INDICATOR_Z_CP = 0x1F1FF;
    private static final int JOKER_CP = 0x1F0CF;
    private static final int STOP_SIGN_CP = 0x1F6D1;
    private static final int TIMEOUT_MS = 2 * 60 * 1000;

    private final Set<Character> guessedLetters = new HashSet<>();
    private final MessageChannel channel;
    private final String player;
    private final boolean coop;

    private Timer timer = new Timer();
    private String embedMessageId;
    private String word;
    private String wordDefinition;
    private int attempts;

    public Hangman(String player, MessageChannel channel, boolean coop) {
        this.channel = channel;
        this.player = player;
        this.coop = coop;
    }

    public boolean initialize() {
        word = generateWord();
        if (word == null) {
            channel.sendMessage("I'm unable to generate a random word").queue();
            return false;
        }

        // Set timeout timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endGame(GameState.TIMEOUT);
            }
        }, TIMEOUT_MS);

        word = word.toUpperCase();
        channel.sendMessageEmbeds(createGameEmbed(GameState.ONGOING).build()).queue(m -> embedMessageId = m.getId());
        return true;
    }

    public void handleEvent(Event event) {
        if (event instanceof MessageReactionAddEvent e) {
            handleMessageReactionAddEvent(e);
        }
    }

    private void handleMessageReactionAddEvent(MessageReactionAddEvent event) {
        // Check message
        EmojiUnion emoji = event.getEmoji();
        if (!event.getMessageId().equals(embedMessageId)) return;
        if (emoji.getType() == Emoji.Type.CUSTOM) return;

        // Check sender
        User sender = event.getUser();
        if (sender == null) return;
        if (sender.isBot()) return;
        if ((!coop) && (!sender.getAsTag().equals(player))) return;

        // Reset timeout timer
        resetTimer();

        // Get code point from emoji
        int codePoint = emoji.getName().codePointAt(0);

        // Check if stop
        if (codePoint == STOP_SIGN_CP) {
            if (!sender.getAsTag().equals(player)) return;

            endGame(GameState.DEFEAT);
            return;
        }

        // Check if joker
        if (codePoint == JOKER_CP) {
            if (!sender.getAsTag().equals(player)) return;
            if (wordDefinition != null) return;

            // Get joker
            wordDefinition = getWordDefinition();

            // Send embed
            channel.retrieveMessageById(embedMessageId).queue(m -> {
                m.clearReactions().queue();
                m.editMessageEmbeds(createGameEmbed(GameState.ONGOING).build()).queue();
            });

            return;
        }

        // Check if emoji is a regional indicator emoji and get the correlating character
        Character character = regionalIndicatorCPToChar(codePoint);
        if (character == null) {
            return;
        }

        // Add letter
        guessedLetters.add(character);

        if (word.indexOf(character) == -1) {
            // Character doesn't exist in the word
            attempts++;
            if (attempts == 7) {
                // Player didn't manage to guess the word
                endGame(GameState.DEFEAT);
                return;
            }
        } else if (!showWord().contains("_")) {
            // Player managed to guess the word
            endGame(GameState.WIN);
            return;
        }

        // Send embed
        channel.retrieveMessageById(embedMessageId).queue(m -> {
            m.clearReactions().queue();
            m.editMessageEmbeds(createGameEmbed(GameState.ONGOING).build()).queue();
        });
    }

    private void resetTimer() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endGame(GameState.TIMEOUT);
            }
        }, TIMEOUT_MS);
    }

    private String getWordDefinition() {
        JSONArray response = (JSONArray) APIRequest.getJson(String.format(API_DICTIONARY, word, Constants.DICTIONARY_API_KEY));
        if ((response == null) || (response.isEmpty())) {
            return "_Unknown_";
        }

        // Check if one definition
        if (response.length() == 1) {
            return response.getString(0);
        }

        // If multiple definitions
        JSONObject objDefinition = response.getJSONObject(0);
        JSONArray shortDefinitions = objDefinition.getJSONArray("shortdef");
        if ((shortDefinitions == null) || (shortDefinitions.isEmpty())) {
            return "_Unknown_";
        }

        int nShortDefinitions = shortDefinitions.length();
        int randomN = ThreadLocalRandom.current().nextInt(nShortDefinitions);
        return shortDefinitions.getString(randomN);
    }

    private Character regionalIndicatorCPToChar(int codepoint) {
        if (codepoint < REGIONAL_INDICATOR_A_CP) return null;
        if (codepoint > REGIONAL_INDICATOR_Z_CP) return null;
        int relativeChar = codepoint - REGIONAL_INDICATOR_A_CP;
        return (char)((char) relativeChar + 'A');
    }

    private void endGame(GameState state) {
        timer.cancel();
        channel.retrieveMessageById(embedMessageId).queue(m -> {
            m.clearReactions().queue();
            m.editMessageEmbeds(createGameEmbed(state).build()).queue();
        });
        HangmanCmd.getInstance().removePlayer(player);
    }

    private String generateWord() {
        String response = APIRequest.getString(API_RANDOM_WORD);
        if (response == null) {
            return null;
        }

        // Remove brackets and quotation marks
        return response.substring(2, response.length() - 2);
    }

    private String showWord() {
        return word.chars()
                .mapToObj(c -> (guessedLetters.contains((char) c) ? (char) c + " " : "_ "))
                .collect(Collectors.joining());
    }

    private String revealWord() {
        return word.chars()
                .mapToObj(c -> ((char) c + " "))
                .collect(Collectors.joining());
    }

    private String createGuessedWords() {
        return guessedLetters.stream().sorted().map(c -> c + " ").collect(Collectors.joining());
    }

    private String createDescription() {
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

    private EmbedBuilder createGameEmbed(GameState state) {
        EmbedBuilder gameEmbed = new EmbedBuilder();
        gameEmbed.setTitle(state.getTitle());
        gameEmbed.setColor(Color.BLUE);
        gameEmbed.setDescription(createDescription());
        gameEmbed.addField("Word", "```" + (state == GameState.ONGOING ? showWord() : revealWord()) + "```", false);
        gameEmbed.addField("Used Letters", createGuessedWords(), false);
        if (state == GameState.ONGOING) {
            if (wordDefinition != null) {
                gameEmbed.addField("Hint", wordDefinition, false);
            }
            gameEmbed.addField(
                    "How To Play",
                    """
                    React with emojis (e.g. \uD83C\uDDE6, \uD83C\uDDE7) to make a guess
                    React with the joker (🃏) to get a hint
                    React with the stop sign (🛑) to end the game
                    """, false);
        }
        return gameEmbed;
    }
}
