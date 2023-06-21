package de.tosoxdev.tosoxjr.commands.csstats;

import de.tosoxdev.tosoxjr.commands.ICommand;
import de.tosoxdev.tosoxjr.utils.APIRequest;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.util.List;

public class CSStatsCmd implements ICommand {
    private final CSStats csStats = new CSStats();

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        String user = ArgumentParser.get(args, 0);
        if (user == null) {
            String msg = String.format("Please use the correct syntax: %scsstats <userid/userurl> <optional: stat>", Constants.BOT_PREFIX);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        String userid = csStats.getID64(user);
        if (userid == null) {
            String msg = String.format("Couldn't find user id from user url: %s", user);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        JSONObject userStats = csStats.getStatistics(userid);
        if (userStats == null) {
            String msg = String.format("Couldn't find user: %s", userid);
            event.getChannel().sendMessage(msg).queue();
            return;
        } else if (userStats.isEmpty()) {
            String msg = String.format("User '%s' has a private or friends only profile", userid);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        String stat = ArgumentParser.get(args, 1);
        if (stat == null) {
            String totalKills = csStats.getStatistic(userStats, "total_kills");
            String totalDeaths = csStats.getStatistic(userStats, "total_deaths");
            String kd = String.valueOf(Integer.parseInt(totalKills) / Integer.parseInt(totalDeaths));

            event.getChannel().sendMessage("").queue();
            return;
        }

        String statistic = csStats.getStatistic(userStats, stat);
        if (statistic == null) {
            String msg = String.format("Couldn't find statistic: %s", stat);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        event.getChannel().sendMessage(statistic).queue();
    }

    @Override
    public String getName() {
        return "csstats";
    }

    @Override
    public String getHelp() {
        return "Get some CS:GO statistics about the given player";
    }
}
