package de.tosoxdev.tosoxjr.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public abstract class CommandBase {
    private final String name;
    private final String description;
    private final List<OptionData> options;

    public CommandBase(String name, String description, List<OptionData> options) {
        this.name = name;
        this.description = description;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public abstract void handle(SlashCommandInteractionEvent event);
}
