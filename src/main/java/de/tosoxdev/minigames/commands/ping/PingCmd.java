package de.tosoxdev.minigames.commands.ping;

import de.tosoxdev.minigames.commands.CommandContext;
import de.tosoxdev.minigames.commands.ICommand;
import net.dv8tion.jda.api.JDA;

public class PingCmd implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        JDA jda = commandContext.getJDA();
        jda.getRestPing().queue((ping) -> commandContext.getChannel().sendMessage("This little maneuver is gonna cost us " + ping + " ms").queue());
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "Shows current ping of the bot to the discord server";
    }
}
