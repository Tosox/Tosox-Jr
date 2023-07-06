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
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Hangman {
    public static final HashMap<String, String> RANDOM_WORD_APIS = new HashMap<>(Map.of(
            "en", "https://capitalizemytitle.com/wp-content/tools/random-word/en/nouns.txt",
            "de", "https://capitalizemytitle.com/wp-content/tools/random-word/de/nouns.txt"
    ));
    private static final HashMap<String, List<String>> RANDOM_WORD_LIST = new HashMap<>();

    static {
        for (Map.Entry<String, String> entry : RANDOM_WORD_APIS.entrySet()) {
            String response = APIRequest.getString(entry.getValue());
            RANDOM_WORD_LIST.put(entry.getKey(), response != null ? List.of(response.split(",")) : null);
        }
    }

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
    private final String language;

    private Timer timer = new Timer();
    private String embedMessageId;
    private String word;
    private String wordDefinition;
    private int attempts;

    public Hangman(String player, MessageChannel channel, boolean coop, String language) {
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

        // Set timeout timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endGame(GameState.TIMEOUT);
            }
        }, TIMEOUT_MS);

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

        // Check if word exists in dictionary
        JSONObject objDefinition;
        try {
            objDefinition = response.getJSONObject(0);
        } catch (JSONException e) {
            return "_Unknown_";
        }

        // If multiple definitions
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
        List<String> words = RANDOM_WORD_LIST.getOrDefault(language.toLowerCase(), RANDOM_WORD_LIST.get("en"));
        int randomIdx = ThreadLocalRandom.current().nextInt(words.size());
        String randomWord = words.get(randomIdx);
        return randomWord != null ? randomWord.toUpperCase().replace("Ä", "AE").replace("Ö", "OE").replace("Ü", "UE") : null;
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
        gameEmbed.setTitle((coop ? "[CO-OP] " : "") + state.getTitle());
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
                    - React with emojis (e.g. \uD83C\uDDE6, \uD83C\uDDE7) to make a guess
                    - React with the joker (🃏) to get a hint
                    - React with the stop sign (🛑) to end the game
                    """, false);
        }
        return gameEmbed;
    }
}
