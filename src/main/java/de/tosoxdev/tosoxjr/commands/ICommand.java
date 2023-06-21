package de.tosoxdev.tosoxjr.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface ICommand {
    void handle(MessageReceivedEvent event, List<String> args);
    String getName();
    String getHelp();
}
