package de.tosoxdev.minigames.commands.say;

import de.tosoxdev.minigames.commands.CommandContext;
import de.tosoxdev.minigames.commands.ICommand;
import de.tosoxdev.minigames.utils.Constants;

public class SayCmd implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        String sayMsg = commandContext.event().getMessage().getContentDisplay().substring(Constants.BOT_PREFIX.length() + getName().length());
        commandContext.event().getChannel().sendMessage(sayMsg).queue();
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
