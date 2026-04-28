package de.tosox.tosoxjr.command.commands;

import de.tosox.tosoxjr.command.CommandBase;
import de.tosox.tosoxjr.service.CSStatsService;
import de.tosox.tosoxjr.util.ArgumentParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CSStatsCommand extends CommandBase {
    private static final String API_FLAGS = "https://flagsapi.com/%s/flat/32.png";
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final CSStatsService csStats;

    public CSStatsCommand(CSStatsService csStats) {
        super("cs-stats", "Get CS:GO statistics for a player", List.of(
                new OptionData(OptionType.STRING, "user", "SteamID64 or vanity URL", true),
                new OptionData(OptionType.STRING, "stat", "Specific statistic name", false)
        ));
        this.csStats = csStats;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String userInput = ArgumentParser.getString(event.getOption("user"), "");
        String requestedStat = ArgumentParser.getString(event.getOption("stat"), null);

        csStats.resolveSteamId64(userInput).ifPresentOrElse(steamId -> {
            Optional<JSONObject> userStatsOpt = csStats.fetchUserStats(steamId);
            if (userStatsOpt.isEmpty()) {
                event.reply("The requested user set their 'Game Details' to private or friends only").queue();
                return;
            }
            JSONObject userStats = userStatsOpt.get();

            if (requestedStat != null) {
                csStats.getStat(userStats, requestedStat).ifPresentOrElse(
                        value -> event.reply("📊 **" + requestedStat + "**: " + value).queue(),
                        () -> event.reply("❌ Statistic not found: `" + requestedStat + "`").queue()
                );
                return;
            }

            sendStatsEmbed(event, userStats, steamId);

        }, () -> event.reply("The requested user doesn't exist").queue());
    }

    private void sendStatsEmbed(SlashCommandInteractionEvent event, JSONObject stats, String steamId) {
        Optional<JSONObject> profileOpt = csStats.fetchProfileInfo(steamId);
        if (profileOpt.isEmpty()) {
            event.reply("⚠ Could not fetch profile info for user.").queue();
            return;
        }
        JSONObject profile = profileOpt.get();

        String kills = csStats.getStat(stats, "total_kills").orElse("0");
        String deaths = csStats.getStat(stats, "total_deaths").orElse("0");
        String headshots = csStats.getStat(stats, "total_kills_headshot").orElse("0");
        String mvps = csStats.getStat(stats, "total_mvps").orElse("0");
        String wins = csStats.getStat(stats, "total_wins").orElse("0");
        String playtime = csStats.getStat(stats, "total_time_played")
                .map(seconds -> String.format("%.2f", Double.parseDouble(seconds) / 3600))
                .orElse("0.0");
        String kd = formatRatio(kills, deaths);
        String hsPct = formatPercentage(headshots, kills);
        String wr = csStats.getStat(stats, "total_matches_won")
                .flatMap(w -> csStats.getStat(stats, "total_matches_played")
                        .map(played -> formatPercentage(w, played)))
                .orElse("0.0");
        String accuracy = csStats.getStat(stats, "total_shots_hit")
                .flatMap(hits -> csStats.getStat(stats, "total_shots_fired")
                        .map(fired -> formatPercentage(hits, fired)))
                .orElse("0.0");

        String profileUrl = profile.optString("profileurl", "");
        String username = profile.optString("personaname", "Unknown");
        String avatarUrl = profile.optString("avatarfull", "");
        String countryCode = profile.optString("loccountrycode", null);
        String flagUrl = (countryCode != null) ? String.format(API_FLAGS, countryCode) : null;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(String.format("**CS2 Stats for %s**", username), null)
                .setColor(Color.ORANGE)
                .setAuthor(username, profileUrl, avatarUrl)
                .setThumbnail(flagUrl)
                .setDescription("Playtime: " + playtime + "h")
                .addField("**K/D**", kd, true)
                .addField("**HS%**", hsPct + "%", true)
                .addField("**Accuracy**", accuracy + "%", true)
                .addField("**MVPs**", mvps, true)
                .addField("**Wins **", wins, true)
                .addField("**Winrate**", wr + "%", true)
                .setFooter("Requested @ " + formatter.format(new Date()), null);

        event.replyEmbeds(embed.build()).queue();
    }

    private String formatRatio(String numerator, String denominator) {
        try {
            double num = Double.parseDouble(numerator);
            double denom = Double.parseDouble(denominator);
            return denom > 0 ? String.format("%.2f", num / denom) : "0.00";
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }

    private String formatPercentage(String part, String total) {
        try {
            double p = Double.parseDouble(part);
            double t = Double.parseDouble(total);
            return t > 0 ? String.format("%.2f", (p / t) * 100) : "0.00";
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }
}
