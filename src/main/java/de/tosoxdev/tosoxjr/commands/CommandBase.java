package de.tosoxdev.tosoxjr.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public abstract class CommandBase {
    private final String name;
    private final String description;

    public CommandBase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void handle(MessageReceivedEvent event, List<String> args);
}
