package de.tosoxdev.minigames.commands.list;

import de.tosoxdev.minigames.commands.CommandManager;
import de.tosoxdev.minigames.commands.ICommand;
import de.tosoxdev.minigames.utils.CommandSorterByName;
import de.tosoxdev.minigames.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class ListCmd implements ICommand {
    private final CommandSorterByName commandSorterByName = new CommandSorterByName();

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        CommandManager cmdManager = CommandManager.getInstance();

        List<ICommand> commands = cmdManager.getCommands();
        commands.sort(commandSorterByName);

        StringBuilder sb = new StringBuilder();
        for (ICommand cmd : commands) {
            sb.append(String.format("%s%s\n", Constants.BOT_PREFIX, cmd.getName()));
        }

        event.getChannel().sendMessage(sb.toString()).queue();
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getHelp() {
        return "Lists all to me known commands";
    }
}
