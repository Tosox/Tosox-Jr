package de.tosoxdev.tosoxjr.commands.list;

import de.tosoxdev.tosoxjr.commands.CommandManager;
import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.utils.CommandSorterByName;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class ListCmd extends CommandBase {
    private final CommandSorterByName commandSorterByName = new CommandSorterByName();

    public ListCmd() {
        super("list", "Lists all to me known commands");
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        CommandManager cmdManager = CommandManager.getInstance();

        List<CommandBase> commands = cmdManager.getCommands();
        commands.sort(commandSorterByName);

        StringBuilder sb = new StringBuilder();
        for (CommandBase cmd : commands) {
            sb.append(String.format("%s%s\n", Constants.BOT_PREFIX, cmd.getName()));
        }

        event.getChannel().sendMessage(sb.toString()).queue();
    }
}
