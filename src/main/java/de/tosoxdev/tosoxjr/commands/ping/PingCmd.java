package de.tosoxdev.tosoxjr.commands.ping;

import de.tosoxdev.tosoxjr.commands.CommandBase;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCmd extends CommandBase {
    public PingCmd() {
        super("ping", "Pong!");
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        event.getChannel().sendMessage("pong!").queue();
    }
}
