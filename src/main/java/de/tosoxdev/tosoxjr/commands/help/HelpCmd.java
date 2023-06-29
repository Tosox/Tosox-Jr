package de.tosoxdev.tosoxjr.commands.help;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.games.GameBase;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class HelpCmd extends CommandBase {
    public HelpCmd() {
        super("help", "Get some information about the requested command");
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        String[] split = event.getMessage().getContentDisplay().substring(Constants.BOT_PREFIX.length()).split(" ");
        List<String> args = Arrays.asList(split).subList(1, split.length);

        String cmdInfo = ArgumentParser.get(args, 0);
        if (cmdInfo == null) {
            String msg = String.format("Please use the correct syntax: %shelp <cmd>", Constants.BOT_PREFIX);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        CommandBase command = Main.getCommandManager().getElement(cmdInfo);
        if (command == null) {
            // Check games
            GameBase game = Main.getGameManager().getElement(cmdInfo);
            if (game == null) {
                event.getChannel().sendMessage("Sorry, I can't find any information about this command").queue();
                return;
            }

            event.getChannel().sendMessage(game.getDescription()).queue();
            return;
        }

        event.getChannel().sendMessage(command.getDescription()).queue();
    }
}
