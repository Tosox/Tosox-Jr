package de.tosoxdev.minigames.commands.ping;

import de.tosoxdev.minigames.commands.CommandContext;
import de.tosoxdev.minigames.commands.ICommand;

public class PingCmd implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        commandContext.getChannel().sendMessage("pong!").queue();
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
