package de.tosoxdev.minigames.commands.help;

import de.tosoxdev.minigames.commands.CommandContext;
import de.tosoxdev.minigames.commands.CommandManager;
import de.tosoxdev.minigames.commands.ICommand;
import net.dv8tion.jda.api.JDA;

public class HelpCmd implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        CommandManager cmdManager = CommandManager.getInstance();

        String arg0;
        try {
            arg0 = commandContext.args().get(0);
        } catch (IndexOutOfBoundsException e) {
            commandContext.getChannel().sendMessage("Please use the correct syntax: $help <cmd>").queue();
            return;
        }

        ICommand command = cmdManager.getCommand(arg0);
        if (command == null) {
            commandContext.getChannel().sendMessage("Sorry, I can't find any information about this command").queue();
            return;
        }

        commandContext.getChannel().sendMessage(command.getHelp()).queue();
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

