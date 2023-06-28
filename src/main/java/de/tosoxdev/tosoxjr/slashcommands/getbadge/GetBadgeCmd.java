package de.tosoxdev.tosoxjr.slashcommands.getbadge;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.slashcommands.SlashCommandBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GetBadgeCmd extends SlashCommandBase {
    public GetBadgeCmd() {
        super("get-badge", "Needs to be run at least one time a month to keep the 'Active Developer' badge");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            Main.getLogger().error("There is no guild available");
            return;
        }

        String ownerId = event.getGuild().getOwnerId();
        String userId = event.getUser().getId();
        if (!userId.equalsIgnoreCase(ownerId)) {
            event.reply("Don't tell me what to do").queue();
            return;
        }

        event.reply("The command was executed successfully :)").queue();
    }
}
