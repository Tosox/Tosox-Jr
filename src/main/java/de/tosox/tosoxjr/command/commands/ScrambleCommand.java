package de.tosox.tosoxjr.command.commands;

import de.tosox.tosoxjr.command.CommandBase;
import de.tosox.tosoxjr.game.GameManager;
import de.tosox.tosoxjr.game.scramble.ScrambleGame;
import de.tosox.tosoxjr.service.ScrambleService;
import de.tosox.tosoxjr.util.ArgumentParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class ScrambleCommand extends CommandBase {
    private static final int MAX_GAMES = 10;

    private final GameManager gameManager;
    private final ScrambleService scrambleService;
    private final String languageListResponse;

    public ScrambleCommand(GameManager gameManager, ScrambleService scrambleService) {
        super("scramble", "Play a game of Scramble", List.of(
                new OptionData(OptionType.STRING, "lang", "Decide the langauge of the word. Use 'list' to list all available ones", false),
                new OptionData(OptionType.BOOLEAN, "coop", "Play Scramble with all your friends on the server", false)
        ));
        this.gameManager = gameManager;
        this.scrambleService = scrambleService;

        StringBuilder sb = new StringBuilder("Available languages");
        scrambleService.getAvailableLanguages().forEach(key -> sb.append("\n- ").append(key));
        languageListResponse = sb.toString();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String lang = ArgumentParser.getString(event.getOption("lang"), "");
        if (lang.equalsIgnoreCase("list")) {
            event.reply(languageListResponse).queue();
            return;
        }

        if (!gameManager.canStartNewGame()) {
            event.reply("⚠️ Too many games running right now. Please try again later.").queue();
            return;
        }

        if (gameManager.getActiveGameCount(ScrambleGame.class) >= MAX_GAMES) {
            event.reply("⚠️ Too many Scramble games running right now. Please try again later.").queue();
            return;
        }

        if (gameManager.hasGame(event.getUser())) {
            event.reply("📝 You already have an active game!").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue(hook -> {
            boolean coop = ArgumentParser.getBoolean(event.getOption("coop"), false);
            ScrambleGame game = new ScrambleGame(gameManager, scrambleService,
                    event.getUser(), event.getChannel(), coop, lang);
            if (!game.initialize()) {
                hook.editOriginal("❌ Failed to start Scramble. Try again later.").queue();
                return;
            }
            gameManager.registerGame(event.getUser(), game);
            hook.deleteOriginal().queue();
        });
    }
}
