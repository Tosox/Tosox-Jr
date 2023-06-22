package de.tosoxdev.tosoxjr.commands.csstats;

import de.tosoxdev.tosoxjr.commands.ICommand;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import de.tosoxdev.tosoxjr.utils.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CSStatsCmd implements ICommand {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
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
            String msg = String.format("Couldn't find user: %s", user);
            event.getChannel().sendMessage(msg).queue();
            return;
        } else if (userStats.isEmpty()) {
            String msg = String.format("User '%s' has a private or friends only profile", user);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        String stat = ArgumentParser.get(args, 1);
        if (stat != null) {
            String statistic = csStats.getStatistic(userStats, stat);
            if (statistic == null) {
                String msg = String.format("Couldn't find statistic: %s", stat);
                event.getChannel().sendMessage(msg).queue();
                return;
            }

            event.getChannel().sendMessage(statistic).queue();
            return;
        }

        String kills = csStats.getStatistic(userStats, "total_kills");
        String deaths = csStats.getStatistic(userStats, "total_deaths");

        int nKills = Integer.parseInt(kills);
        int mDeaths = Integer.parseInt(deaths);
        double nKd = (double) nKills / mDeaths;
        String kd = String.format("%.2f", nKd);

        String headshots = csStats.getStatistic(userStats, "total_kills_headshot");
        int nHeadshots = Integer.parseInt(headshots);
        double nHspersentage = ((double) nHeadshots / nKills) * 100;
        String hspercentage = String.format("%.2f", nHspersentage);

        String playtimeSeconds = csStats.getStatistic(userStats, "total_time_played");
        double nPlaytime = Double.parseDouble(playtimeSeconds) / 3600;
        String playtime = String.format("%.2f", nPlaytime);

        String mvps = csStats.getStatistic(userStats, "total_mvps");

        String wins = csStats.getStatistic(userStats, "total_matches_won");
        String matches = csStats.getStatistic(userStats, "total_matches_played");
        int nWins = Integer.parseInt(wins);
        int nMatches = Integer.parseInt(matches);
        double nWr = ((double) nWins / nMatches) * 100;
        String wr = String.format("%.2f", nWr);

        String fired = csStats.getStatistic(userStats, "total_shots_fired");
        String hits = csStats.getStatistic(userStats, "total_shots_hit");
        int nFired = Integer.parseInt(fired);
        int nHits = Integer.parseInt(hits);
        double nAccuracy = ((double) nHits / nFired) * 100;
        String accuracy = String.format("%.2f", nAccuracy);

        JSONObject profileData = csStats.getProfileInfos(userid);
        if (profileData == null) {
            String msg = String.format("Couldn't get Steam infos about %s", user);
            event.getChannel().sendMessage(msg).queue();
            return;
        }

        String profileUrl = csStats.getProfileInfo(profileData, "profileurl");
        String username = csStats.getProfileInfo(profileData, "personaname");
        String avatarUrl = csStats.getProfileInfo(profileData, "avatarfull");
        String countryCode = csStats.getProfileInfo(profileData, "loccountrycode");
        String flagUrl = String.format("https://flagsapi.com/%s/flat/32.png", countryCode);

        EmbedBuilder statsEmbed = new EmbedBuilder();
        statsEmbed.setTitle(String.format("**CS:GO Stats for %s**", user), null);
        statsEmbed.setColor(Color.ORANGE);
        statsEmbed.setThumbnail(flagUrl);
        statsEmbed.setAuthor(username, profileUrl, avatarUrl);
        statsEmbed.setDescription("Playtime: " + playtime + "h");
        statsEmbed.addField("**K/D**", kd, true);
        statsEmbed.addField("**Headshot %**", hspercentage + "%", true);
        statsEmbed.addField("**Accuracy**", accuracy + "%", true);
        statsEmbed.addField("**MVPs**", mvps, true);
        statsEmbed.addField("**Wins**", wins, true);
        statsEmbed.addField("**Winrate**", wr + "%", true);
        statsEmbed.setFooter("Request made @ " + formatter.format(new Date()), null);

        event.getChannel().sendMessageEmbeds(statsEmbed.build()).queue();
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
