package de.tosoxdev.tosoxjr.slashcommands;

import de.tosoxdev.tosoxjr.GenericManagerBase;
import de.tosoxdev.tosoxjr.slashcommands.getbadge.GetBadgeCmd;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandManager extends GenericManagerBase<SlashCommandBase, SlashCommandInteractionEvent> {
    public SlashCommandManager() {
        addElement(new GetBadgeCmd());
    }

    public void handle(SlashCommandInteractionEvent event) {
        String command = event.getName();
        SlashCommandBase cmd = getElement(command);
        cmd.handle(event, null);
    }
}
