package de.tosoxdev.minigames.commands.ping;

import de.tosoxdev.minigames.commands.ICommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class PingCmd implements ICommand {
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage("pong!").queue();
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "Pong!";
    }
}
