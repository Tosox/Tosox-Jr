package de.tosoxdev.tosoxjr.commands.list;

import de.tosoxdev.tosoxjr.Main;
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
        List<CommandBase> commands = Main.getCommandManager().getCommands();
        commands.sort(commandSorterByName);

        StringBuilder sb = new StringBuilder("Available Commands:\n```\n");
        for (CommandBase cmd : commands) {
            sb.append(String.format("%s%s\n", Constants.BOT_PREFIX, cmd.getName()));
        }
        sb.append("```");

        event.getChannel().sendMessage(sb.toString()).queue();
    }
}
