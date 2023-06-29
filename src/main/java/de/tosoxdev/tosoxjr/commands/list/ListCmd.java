package de.tosoxdev.tosoxjr.commands.list;

import de.tosoxdev.tosoxjr.GenericCommandBase;
import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.games.GameBase;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListCmd extends CommandBase {
    public ListCmd() {
        super("list", "Lists all to me known commands");
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        List<String> commands = Main.getCommandManager().getElements().stream()
                .map(GenericCommandBase::getName)
                .collect(Collectors.toList());
        commands.addAll(Main.getGameManager().getElements().stream().map(GameBase::getName).toList());
        Collections.sort(commands);

        StringBuilder sb = new StringBuilder("Available Commands:\n```\n");
        for (String cmd : commands) {
            sb.append(String.format("%s%s\n", Constants.BOT_PREFIX, cmd));
        }
        sb.append("```");

        event.getChannel().sendMessage(sb.toString()).queue();
    }
}
