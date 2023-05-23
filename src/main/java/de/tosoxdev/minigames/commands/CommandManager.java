package de.tosoxdev.minigames.commands;

import de.tosoxdev.minigames.commands.ping.PingCmd;
import de.tosoxdev.minigames.commands.say.SayCmd;
import de.tosoxdev.minigames.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new PingCmd());
        addCommand(new SayCmd());
    }

    private void addCommand(ICommand cmd) {
        boolean commandName = this.commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(cmd.getName()));
        if (commandName) {
            throw new IllegalArgumentException("Command is already in the list");
        }
        commands.add(cmd);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search) {
        for (ICommand cmd : this.commands) {
            if (cmd.getName().equals(search.toLowerCase())) {
                return cmd;
            }
        }
        return null;
    }

    public void handle(MessageReceivedEvent event) {
        // Remove prefix, split arguments
        String[] split = event.getMessage().getContentDisplay().substring(Constants.BOT_PREFIX.length()).split(" ");
        String command = split[0].toLowerCase();
        ICommand cmd = this.getCommand(command);

        event.getChannel().sendTyping().queue();

        if (cmd == null) {
            event.getChannel().sendMessage("Hmmmm, seems like I don't know this command").queue();
            return;
        }

        List<String> args = Arrays.asList(split).subList(1, split.length);
        cmd.handle(new CommandContext(event, args));
    }
}
