package de.tosoxdev.minigames.commands.help;

import de.tosoxdev.minigames.commands.CommandContext;
import de.tosoxdev.minigames.commands.CommandManager;
import de.tosoxdev.minigames.commands.ICommand;
import de.tosoxdev.minigames.utils.Constants;

public class HelpCmd implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        CommandManager cmdManager = CommandManager.getInstance();

        String arg0;
        try {
            arg0 = commandContext.args().get(0);
        } catch (IndexOutOfBoundsException e) {
            String msg = String.format("Please use the correct syntax: %s%s <cmd>", Constants.BOT_PREFIX, getName());
            commandContext.event().getChannel().sendMessage(msg).queue();
            return;
        }

        ICommand command = cmdManager.getCommand(arg0);
        if (command == null) {
            commandContext.event().getChannel().sendMessage("Sorry, I can't find any information about this command").queue();
            return;
        }

        commandContext.event().getChannel().sendMessage(command.getHelp()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Get some information about the requested command";
    }
}

