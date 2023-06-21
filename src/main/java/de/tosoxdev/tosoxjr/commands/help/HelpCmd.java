package de.tosoxdev.tosoxjr.commands.help;

import de.tosoxdev.tosoxjr.commands.CommandManager;
import de.tosoxdev.tosoxjr.commands.ICommand;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class HelpCmd implements ICommand {
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        CommandManager cmdManager = CommandManager.getInstance();

        String cmdInfo = ArgumentParser.get(args, 0);
        if (cmdInfo == null) {
            String msg = String.format("Please use the correct syntax: %shelp <cmd>", Constants.BOT_PREFIX);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        ICommand command = cmdManager.getCommand(cmdInfo);
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
