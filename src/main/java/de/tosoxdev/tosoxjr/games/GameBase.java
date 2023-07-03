package de.tosoxdev.tosoxjr.games;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public abstract class GameBase {
    private final String name;
    private final String description;
    private final List<OptionData> options;

    public GameBase(String name, String description, List<OptionData> options) {
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

    public abstract void handle(Event event);

    public abstract void run(MessageReceivedEvent event);
}
