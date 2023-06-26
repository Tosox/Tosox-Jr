package de.tosoxdev.tosoxjr.commands.hangman;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.rmi.Remote;
import java.util.HashSet;
import java.util.Set;

public class Hangman {
    private static Hangman instance;

    private static final String API_RANDOM_WORD = "https://random-word-api.vercel.app/api?words=1";
    private static final String API_DICTIONARY = "https://www.dictionaryapi.com/api/v3/references/collegiate/json/%s?key=%s";

    private final Set<Character> guessedLetters = new HashSet<>();
    private String embedMessageId;
    private String player;
    private String word;

    public Hangman() {
        instance = this;
    }

    public boolean newGame(String player, MessageChannel channel) {
        word = generateWord();
        if (word == null) {
            return false;
        }

        this.player = player;
        guessedLetters.clear();

        StringBuilder sb = new StringBuilder();
        guessedLetters.forEach(l -> sb.append(l).append(" "));

        EmbedBuilder gameEmbed = new EmbedBuilder();
        gameEmbed.setTitle("Hangman");
        gameEmbed.addField("Used letters", sb.toString(), false);

        channel.sendMessageEmbeds(gameEmbed.build()).queue(m -> {
            embedMessageId = m.getId();
        });

        return true;
    }

    public void handle(MessageReactionAddEvent event) {
        if (!event.getMessageId().equals(embedMessageId)) return;
        if (event.getEmoji().getType() == Emoji.Type.CUSTOM) return;

        User sender = event.getUser();
        if (sender == null) return;
        if (sender.isBot()) return;
        if (!sender.getAsTag().equals(player)) return;

        event.getChannel().retrieveMessageById(embedMessageId).queue(m -> {
            String emoji = event.getEmoji().getName();
            int codePoint = emoji.codePointAt(0);
            System.out.println(codePoint);
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

    public boolean isRunning() {
        return player != null;
    }

    public static Hangman getInstance() {
        return instance;
    }
}
