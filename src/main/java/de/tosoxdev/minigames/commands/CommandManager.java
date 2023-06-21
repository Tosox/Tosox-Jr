package de.tosoxdev.minigames.commands;

import de.tosoxdev.minigames.commands.help.HelpCmd;
import de.tosoxdev.minigames.commands.list.ListCmd;
import de.tosoxdev.minigames.commands.ping.PingCmd;
import de.tosoxdev.minigames.commands.say.SayCmd;
import de.tosoxdev.minigames.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    private static CommandManager instance;
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        instance = this;
        addCommand(new PingCmd());
        addCommand(new SayCmd());
        addCommand(new HelpCmd());
        addCommand(new ListCmd());
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    private void addCommand(ICommand cmd) {
        boolean commandName = this.commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(cmd.getName()));
        if (commandName) {
            throw new IllegalArgumentException("Found duplicate in the command list");
        }
        commands.add(cmd);
    }

    @Nullable
    public ICommand getCommand(String search) {
        return this.commands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(search))
                .findFirst()
                .orElse(null);
    }

    public void handle(MessageReceivedEvent event) {
        // Remove prefix, split arguments
        String[] split = event.getMessage().getContentDisplay().substring(Constants.BOT_PREFIX.length()).split(" ");
        String command = split[0].toLowerCase();
        ICommand cmd = this.getCommand(command);

        event.getChannel().sendTyping().queue();

        if (cmd == null) {
            event.getChannel().sendMessage("Seems like I don't know this command").queue();
            return;
        }

        List<String> args = Arrays.asList(split).subList(1, split.length);
        cmd.handle(new CommandContext(event, args));
    }

    public static CommandManager getInstance() {
        return instance;
    }
}
