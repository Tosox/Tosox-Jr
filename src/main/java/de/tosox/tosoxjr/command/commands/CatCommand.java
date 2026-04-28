package de.tosox.tosoxjr.command.commands;

import de.tosox.tosoxjr.command.CommandBase;
import de.tosox.tosoxjr.service.CatService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CatCommand extends CommandBase {
    private final CatService catService;

    public CatCommand(CatService catService) {
        super("cat", "Get a random picture of a cat", null);
        this.catService = catService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        catService.getRandomCatImage().ifPresentOrElse(
                url -> event.getHook().sendMessage(url).queue(),
                () -> event.getHook().sendMessage("Unable to fetch a cat image right now 😿").queue()
        );
    }
}
