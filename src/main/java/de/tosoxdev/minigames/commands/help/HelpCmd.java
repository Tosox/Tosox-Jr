package de.tosoxdev.minigames.commands.help;

import de.tosoxdev.minigames.commands.CommandManager;
import de.tosoxdev.minigames.commands.ICommand;
import de.tosoxdev.minigames.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class HelpCmd implements ICommand {
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        CommandManager cmdManager = CommandManager.getInstance();

        String arg0;
        try {
            arg0 = args.get(0);
        } catch (IndexOutOfBoundsException e) {
            String msg = String.format("Please use the correct syntax: %s%s <cmd>", Constants.BOT_PREFIX, getName());
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        ICommand command = cmdManager.getCommand(arg0);
        if (command == null) {
            event.getChannel().sendMessage("Sorry, I can't find any information about this command").queue();
            return;
        }

        event.getChannel().sendMessage(command.getHelp()).queue();
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
