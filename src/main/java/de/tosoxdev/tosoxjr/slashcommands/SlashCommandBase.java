package de.tosoxdev.tosoxjr.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class SlashCommandBase {
    private final String name;
    private final String description;

    public SlashCommandBase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void handle(SlashCommandInteractionEvent event);
}
