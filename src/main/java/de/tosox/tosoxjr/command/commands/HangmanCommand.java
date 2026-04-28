package de.tosox.tosoxjr.command.commands;

import de.tosox.tosoxjr.command.CommandBase;
import de.tosox.tosoxjr.game.GameManager;
import de.tosox.tosoxjr.game.hangman.HangmanGame;
import de.tosox.tosoxjr.service.HangmanService;
import de.tosox.tosoxjr.util.ArgumentParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class HangmanCommand extends CommandBase {
    private static final int MAX_GAMES = 20;

    private final GameManager gameManager;
    private final HangmanService hangmanService;
    private final String languageListResponse;

    public HangmanCommand(GameManager gameManager, HangmanService hangmanService) {
        super("hangman", "Play a game of Hangman", List.of(
                new OptionData(OptionType.STRING, "lang", "Decide the langauge of the word. Use 'list' to list all available ones", false),
                new OptionData(OptionType.BOOLEAN, "coop", "Play Hangman with all your friends on the server", false)
        ));
        this.gameManager = gameManager;
        this.hangmanService = hangmanService;

        StringBuilder sb = new StringBuilder("Available languages");
        hangmanService.getAvailableLanguages().forEach(key -> sb.append("\n- ").append(key));
        languageListResponse = sb.toString();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String lang = ArgumentParser.getString(event.getOption("lang"), "");
        boolean coop = ArgumentParser.getBoolean(event.getOption("coop"), false);

        if (lang.equalsIgnoreCase("list")) {
            event.reply(languageListResponse).queue();
            return;
        }

        if (!gameManager.canStartNewGame()) {
            event.reply("⚠️ Too many games running right now. Please try again later.").queue();
            return;
        }

        if (gameManager.getActiveGameCount(HangmanGame.class) >= MAX_GAMES) {
            event.reply("⚠️ Too many Hangman games running right now. Please try again later.").queue();
            return;
        }

        if (gameManager.hasGame(event.getUser())) {
            event.reply("📝 You already have an active Hangman game!").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue(hook -> {
            HangmanGame game = new HangmanGame(gameManager,  hangmanService,
                    event.getUser(), event.getChannel(), lang, coop);
            if (!game.initialize()) {
                hook.editOriginal("❌ Failed to start Hangman. Try again later.").queue();
            }

            gameManager.registerGame(event.getUser(), game);
            hook.deleteOriginal().queue();
        });
    }
}
