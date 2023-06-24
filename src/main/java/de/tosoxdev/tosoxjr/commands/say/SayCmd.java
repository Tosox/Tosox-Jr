package de.tosoxdev.tosoxjr.commands.say;

import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class SayCmd extends CommandBase {
    public SayCmd() {
        super("say", "Repeats the given message");
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        String sayMsg = event.getMessage().getContentDisplay().substring(Constants.BOT_PREFIX.length() + getName().length());
        event.getChannel().sendMessage(sayMsg).queue();
    }
}
