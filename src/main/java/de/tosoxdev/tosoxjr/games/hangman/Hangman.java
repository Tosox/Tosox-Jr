package de.tosoxdev.tosoxjr.games.hangman;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Hangman {
    private static Hangman instance;

    private static final String API_RANDOM_WORD = "https://random-word-api.vercel.app/api?words=1";
    private static final String API_DICTIONARY = "https://www.dictionaryapi.com/api/v3/references/collegiate/json/%s?key=%s";

    private static final int REGIONAL_INDICATOR_A_CP = 0x1F1E6;
    private static final int REGIONAL_INDICATOR_Z_CP = 0x1F1FF;
    private static final int JOKER_CP = 0x1F0CF;

    private final Set<Character> guessedLetters = new HashSet<>();
    private MessageChannel channel;
    private String embedMessageId;
    private String player;
    private String word;
    private String wordDefinition;
    private int attempts;

    public Hangman() {
        instance = this;
    }

    public boolean newGame(String player, MessageChannel channel) {
        word = generateWord();
        if (word == null) {
            return false;
        }

        word = word.toUpperCase();
        this.channel = channel;
        this.player = player;

        channel.sendMessageEmbeds(createGameEmbed(false).build()).queue(m -> embedMessageId = m.getId());

        return true;
    }

    public void handle(MessageReactionAddEvent event) {
        // Check message
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

        // Check if joker
        if (codePoint == JOKER_CP) {
            if (wordDefinition != null) return;

            // Get joker
            wordDefinition = getWordDefinition();

            // Send embed
            channel.retrieveMessageById(embedMessageId).queue(m -> {
                m.clearReactions().queue();
                m.editMessageEmbeds(createGameEmbed(false).build()).queue();
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
                endGame();
                return;
            }
        } else if (!createWordTemplate(false).contains("_")) {
            // Player managed to guess the word
            endGame();
            return;
        }

        // Send embed
        channel.retrieveMessageById(embedMessageId).queue(m -> {
            m.clearReactions().queue();
            m.editMessageEmbeds(createGameEmbed(false).build()).queue();
        });
    }

    private String getWordDefinition() {
        JSONArray response = (JSONArray) APIRequest.getJson(String.format(API_DICTIONARY, word, Constants.DICTIONARY_API_KEY));
        if ((response == null) || (response.isEmpty())) {
            return "_Unknown_";
        }

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

    private void endGame() {
        // Send embed
        channel.retrieveMessageById(embedMessageId).queue(m -> {
            m.clearReactions().queue();
            m.editMessageEmbeds(createGameEmbed(true).build()).queue();

            // Reset variables
            guessedLetters.clear();
            channel = null;
            embedMessageId = null;
            player = null;
            word = null;
            wordDefinition = null;
            attempts = 0;
        });
    }

    private String generateWord() {
        String response = APIRequest.getString(API_RANDOM_WORD);
        if (response == null) {
            return null;
        }

        // Remove brackets and quotation marks
        return response.substring(2, response.length() - 2);
    }

    private String createWordTemplate(boolean show) {
        return word.chars()
                .mapToObj(c -> show ? ((char) c + " ") : (guessedLetters.contains((char) c) ? (char) c + " " : "_ "))
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

    private EmbedBuilder createGameEmbed(boolean isEndScreen) {
        EmbedBuilder gameEmbed = new EmbedBuilder();
        gameEmbed.setTitle(!isEndScreen ? "Hangman" : (attempts == 7 ? "You lost!" : "You won!"));
        gameEmbed.setColor(Color.BLUE);
        gameEmbed.setDescription(createDescription());
        gameEmbed.addField("Word", "```" + createWordTemplate(isEndScreen) + "```", false);
        gameEmbed.addField("Used Letters", createGuessedWords(), false);
        if (!isEndScreen) {
            if (wordDefinition != null) {
                gameEmbed.addField("Hint", wordDefinition, false);
            }
            gameEmbed.addField("How To Play", "React with emojis (e.g. \uD83C\uDDE6, \uD83C\uDDE7) to make a guess\nReact with the joker (🃏) to get a hint", false);
        }
        return gameEmbed;
    }

    public boolean isRunning() {
        return player != null;
    }

    public static Hangman getInstance() {
        return instance;
    }
}
