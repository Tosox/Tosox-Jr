package de.tosoxdev.tosoxjr.slashcommands;

import de.tosoxdev.tosoxjr.GenericCommandBase;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class SlashCommandBase extends GenericCommandBase<SlashCommandInteractionEvent> {
    public SlashCommandBase(String name, String description) {
        super(name, description);
    }
}
