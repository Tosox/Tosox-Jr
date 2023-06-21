package de.tosoxdev.tosoxjr.commands.csstats;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class CSStats {
    private static final String STEAM_API_KEY = System.getenv("STEAM_API_KEY");
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

        String query = String.format("http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=%s&vanityurl=%s", STEAM_API_KEY, userid);
        JSONObject response = APIRequest.get(query);
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        JSONObject objResponse = response.getJSONObject("response");
        int success = objResponse.getInt("success");
        if (success != 1) {
            System.out.printf("[ERROR]: Success was not '1' when trying to get statistics for %s\n", userid);
            return null;
        }

        return objResponse.getString("steamid");
    }

    public JSONObject getStatistics(String userid) {
        String query = String.format("http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v2/?appid=%s&key=%s&steamid=%s", CSGO_APP_ID, STEAM_API_KEY, userid);
        return APIRequest.get(query);
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
}
