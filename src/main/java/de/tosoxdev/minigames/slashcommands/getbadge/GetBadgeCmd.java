package de.tosoxdev.minigames.slashcommands.getbadge;

import de.tosoxdev.minigames.slashcommands.ISlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GetBadgeCmd implements ISlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            System.out.println("[ERROR]: The guild was null when trying to run 'get-badge'");
            return;
        }

        String ownerId = event.getGuild().getOwnerId();
        String userId = event.getUser().getId();
        if (!userId.equalsIgnoreCase(ownerId)) {
            event.reply("You don't have the permission to run this command").queue();
            return;
        }

        event.reply("The command was successfully executed :)").queue();
    }

    @Override
    public String getName() {
        return "get-badge";
    }

    @Override
    public String getDescription() {
        return "Needs to be run at least one time a month to keep the 'Active Developer' badge";
    }
}
