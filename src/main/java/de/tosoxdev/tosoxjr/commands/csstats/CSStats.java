package de.tosoxdev.tosoxjr.commands.csstats;

import de.tosoxdev.tosoxjr.Main;
import de.tosoxdev.tosoxjr.utils.APIRequest;
import de.tosoxdev.tosoxjr.utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

public class CSStats {
    private static final String API_VANITY_URL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=%s&vanityurl=%s";
    private static final String API_GAME_STATS = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v2/?appid=%s&key=%s&steamid=%s";
    private static final String API_USER_PROFILE = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s";
    private static final String CSGO_APP_ID = "730";

    private boolean isID64(String userid) {
        try {
            Long.parseLong(userid);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getID64(String userid) {
        if (isID64(userid)) {
            return userid;
        }

        String query = String.format(API_VANITY_URL, Constants.STEAM_API_KEY, userid);
        JSONObject response = (JSONObject) APIRequest.getJson(query);
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        JSONObject objResponse = response.getJSONObject("response");
        int success = objResponse.getInt("success");
        if (success != 1) {
            Main.getLogger().error("'success' was not '1' when trying to get statistics for {}", userid);
            return null;
        }

        return objResponse.getString("steamid");
    }

    public JSONObject getStatistics(String userid64) {
        String query = String.format(API_GAME_STATS, CSGO_APP_ID, Constants.STEAM_API_KEY, userid64);
        return (JSONObject) APIRequest.getJson(query);
    }

    public String getStatistic(JSONObject statistics, String statistic) {
        JSONObject playerStats = statistics.getJSONObject("playerstats");
        JSONArray stats = playerStats.getJSONArray("stats");

        for (Object objStat : stats) {
            JSONObject stat = (JSONObject) objStat;
            if (stat.getString("name").equalsIgnoreCase(statistic)) {
                return String.valueOf(stat.getInt("value"));
            }
        }

        return null;
    }

    public JSONObject getProfileInfos(String userid64) {
        String query = String.format(API_USER_PROFILE, Constants.STEAM_API_KEY, userid64);
        JSONObject response = (JSONObject) APIRequest.getJson(query);
        if (response == null) {
            return null;
        }

        JSONObject objResponse = response.getJSONObject("response");
        JSONArray players = objResponse.optJSONArray("players");
        if ((players == null) || players.isEmpty()) {
            return null;
        }

        return (JSONObject) players.get(0);
    }

    public String getProfileInfo(JSONObject infos, String info) {
        boolean hasInfo = infos.has(info);
        return hasInfo ? String.valueOf(infos.get(info)) : null;
    }
}
