package de.tosox.tosoxjr.service;

import de.tosox.tosoxjr.util.Constants;
import de.tosox.tosoxjr.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CSStatsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSStatsService.class);

	private static final String API_VANITY_URL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=%s&vanityurl=%s";
	private static final String API_GAME_STATS = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v2/?appid=730&key=%s&steamid=%s";
	private static final String API_USER_PROFILE = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s";

	private final String apiKey;

	public CSStatsService() {
		// TODO: Put in constructor
		this.apiKey = Constants.STEAM_API_KEY;
	}

	public Optional<String> resolveSteamId64(String user) {
		if (isSteamId64(user)) {
			return Optional.of(user);
		}

		String url = String.format(API_VANITY_URL, apiKey, user);
		JSONObject response = HttpUtil.getJsonObject(url);
		if (response == null) {
			return Optional.empty();
		}

		JSONObject obj = response.optJSONObject("response");
		if ((obj != null) && (obj.optInt("success") == 1)) {
			return Optional.ofNullable(obj.optString("steamid", null));
		}

		LOGGER.warn("Could not resolve vanity URL for user '{}'", user);
		return Optional.empty();
	}

	public Optional<JSONObject> fetchUserStats(String steamId64) {
		String url = String.format(API_GAME_STATS, apiKey, steamId64);
		JSONObject response = HttpUtil.getJsonObject(url);
		if ((response == null) || (response.isEmpty())) {
			return Optional.empty();
		}

		return Optional.of(response);
	}

	public Optional<JSONObject> fetchProfileInfo(String steamId64) {
		String url = String.format(API_USER_PROFILE, apiKey, steamId64);
		JSONObject response = HttpUtil.getJsonObject(url);
		if (response == null) {
			return Optional.empty();
		}

		JSONArray players = response.optJSONObject("response").optJSONArray("players");
		if ((players != null) && (!players.isEmpty())) {
			return Optional.of(players.getJSONObject(0));
		}

		return Optional.empty();
	}

	public Optional<String> getStat(JSONObject statsRoot, String statName) {
		JSONArray statsArray = statsRoot
				.optJSONObject("playerstats")
				.optJSONArray("stats");
		if (statsArray == null) {
			return Optional.empty();
		}

		for (int i = 0; i < statsArray.length(); i++) {
			JSONObject stat = statsArray.getJSONObject(i);
			if (statName.equalsIgnoreCase(stat.optString("name"))) {
				return Optional.of(String.valueOf(stat.optInt("value", 0)));
			}
		}

		return Optional.empty();
	}

	private boolean isSteamId64(String id) {
		return id.matches("\\d{17}");
	}
}
