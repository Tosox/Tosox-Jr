package de.tosoxdev.tosoxjr.commands.say;

import de.tosoxdev.tosoxjr.commands.ICommand;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class SayCmd implements ICommand {
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        String sayMsg = event.getMessage().getContentDisplay().substring(Constants.BOT_PREFIX.length() + getName().length());
        event.getChannel().sendMessage(sayMsg).queue();
    }

    @Override
    public String getName() {
        return "say";
    }

    @Override
    public String getHelp() {
        return "Repeats the given message";
    }
}
