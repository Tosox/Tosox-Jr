package de.tosoxdev.tosoxjr.commands.csstats;

import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CSStatsCmd extends CommandBase {
    private static final String API_FLAGS = "https://flagsapi.com/%s/flat/32.png";
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final CSStats csStats = new CSStats();

    public CSStatsCmd() {
        super("cs-stats", "Get some CS:GO statistics about the given player", List.of(
                new OptionData(OptionType.STRING, "user", "The user id64 or the url of the steam profile", true),
                new OptionData(OptionType.STRING, "stat", "A statistic to retrieve from the user statistics", false)
        ));
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String user = ArgumentParser.getString(event.getOption("user"), "");

        // Check and get steamid64
        String userid = csStats.getID64(user);
        if (userid == null) {
            String msg = String.format("Couldn't find user id from user url: %s", user);
            event.reply(msg).queue();
            return;
        }

        // Get user statistics
        JSONObject userStats = csStats.getStatistics(userid);
        if (userStats == null) {
            String msg = String.format("Couldn't find user: %s", user);
            event.reply(msg).queue();
            return;
        } else if (userStats.isEmpty()) {
            String msg = String.format("User '%s' set 'Game Details' to private or friends only", user);
            event.reply(msg).queue();
            return;
        }

        // Check if a statistic parameter is provided
        String stat = ArgumentParser.getString(event.getOption("stat"), null);
        if (stat != null) {
            // Check if statistic exists
            String statistic = csStats.getStatistic(userStats, stat);
            if (statistic == null) {
                String msg = String.format("Couldn't find statistic: %s", stat);
                event.reply(msg).queue();
                return;
            }

            event.reply(statistic).queue();
            return;
        }

        // Get CS:GO statistics
        String kills = csStats.getStatistic(userStats, "total_kills");
        String deaths = csStats.getStatistic(userStats, "total_deaths");
        String headshots = csStats.getStatistic(userStats, "total_kills_headshot");
        String playtimeSeconds = csStats.getStatistic(userStats, "total_time_played");
        String mvps = csStats.getStatistic(userStats, "total_mvps");
        String wins = csStats.getStatistic(userStats, "total_matches_won");
        String matches = csStats.getStatistic(userStats, "total_matches_played");
        String fired = csStats.getStatistic(userStats, "total_shots_fired");
        String hits = csStats.getStatistic(userStats, "total_shots_hit");

        // Calculate custom statistics
        String kd = String.format("%.2f", (double) Integer.parseInt(kills) / Integer.parseInt(deaths));
        String hsPercentage = String.format("%.2f", ((double) Integer.parseInt(headshots) / Integer.parseInt(kills)) * 100);
        String playtime = String.format("%.2f", Double.parseDouble(playtimeSeconds) / 3600);
        String wr = String.format("%.2f", ((double) Integer.parseInt(wins) / Integer.parseInt(matches)) * 100);
        String accuracy = String.format("%.2f", ((double) Integer.parseInt(hits) / Integer.parseInt(fired)) * 100);

        // Get user profile information
        JSONObject profileData = csStats.getProfileInfos(userid); // Shouldn't return null because profile visibility is linked with game details visibility
        String profileUrl = csStats.getProfileInfo(profileData, "profileurl");
        String username = csStats.getProfileInfo(profileData, "personaname");
        String avatarUrl = csStats.getProfileInfo(profileData, "avatarfull");
        String countryCode = csStats.getProfileInfo(profileData, "loccountrycode");
        String flagUrl = String.format(API_FLAGS, countryCode);

        // Build embed
        EmbedBuilder statsEmbed = new EmbedBuilder();
        statsEmbed.setTitle(String.format("**CS:GO Stats for %s**", user), null);
        statsEmbed.setColor(Color.ORANGE);
        statsEmbed.setThumbnail(countryCode != null ? flagUrl : null);
        statsEmbed.setAuthor(username, profileUrl, avatarUrl);
        statsEmbed.setDescription("Playtime: " + playtime + "h");
        statsEmbed.addField("**K/D**", kd, true);
        statsEmbed.addField("**Headshot %**", hsPercentage + "%", true);
        statsEmbed.addField("**Accuracy**", accuracy + "%", true);
        statsEmbed.addField("**MVPs**", mvps, true);
        statsEmbed.addField("**Wins**", wins, true);
        statsEmbed.addField("**Winrate**", wr + "%", true);
        statsEmbed.setFooter("Request made @ " + formatter.format(new Date()), null);

        event.replyEmbeds(statsEmbed.build()).queue();
    }
}
