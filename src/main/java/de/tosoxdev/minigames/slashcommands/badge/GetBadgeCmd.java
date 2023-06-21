package de.tosoxdev.minigames.slashcommands.badge;

import de.tosoxdev.minigames.slashcommands.ISlashCommand;
import de.tosoxdev.minigames.slashcommands.SlashCommandContext;

public class GetBadgeCmd implements ISlashCommand {
    @Override
    public void handle(SlashCommandContext slashCommandContext) {
        String ownerTag = slashCommandContext.getGuild().getOwnerId();
        String userTag = slashCommandContext.getAuthor().getId();
        if (!userTag.equalsIgnoreCase(ownerTag)) {
            slashCommandContext.event().reply("You don't have the permission to run this command").queue();
        }

        slashCommandContext.event().reply("The command was successfully executed :)").queue();
    }

    @Override
    public String getName() {
        return "get-badge";
    }

    @Override
    public String getHelp() {
        return "Needs to be run at least one time a month to keep the 'Active Developer' badge";
    }
}
