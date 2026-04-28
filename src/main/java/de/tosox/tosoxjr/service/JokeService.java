package de.tosox.tosoxjr.service;

import de.tosox.tosoxjr.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

public class JokeService {
	private static final String PUN_API = "https://official-joke-api.appspot.com/jokes/general/random";
	private static final String PROGRAMMING_API = "https://official-joke-api.appspot.com/jokes/programming/random";
	private static final String CHUCK_NORRIS_API = "https://api.chucknorris.io/jokes/random";

	public Optional<String> getPun() {
		JSONArray response = HttpUtil.getJsonArray(PUN_API);
		return parseJokeResponse(response);
	}

	public Optional<String> getProgramming() {
		JSONArray response = HttpUtil.getJsonArray(PROGRAMMING_API);
		return parseJokeResponse(response);
	}

	public Optional<String> getChuckNorris() {
		JSONObject response = HttpUtil.getJsonObject(CHUCK_NORRIS_API);
		if (response != null) {
			return Optional.of(response.getString("value"));
		}
		return Optional.empty();
	}

	private Optional<String> parseJokeResponse(JSONArray response) {
		if ((response != null) && (!response.isEmpty())) {
			JSONObject jokeObject = response.getJSONObject(0);
			String setup = jokeObject.getString("setup");
			String punchline = jokeObject.getString("punchline");
			return Optional.of(String.format("%s\n... %s", setup, punchline));
		}
		return Optional.empty();
	}
}
