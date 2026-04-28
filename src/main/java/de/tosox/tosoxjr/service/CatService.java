package de.tosox.tosoxjr.service;

import de.tosox.tosoxjr.util.HttpUtil;
import org.json.JSONArray;

import java.util.Optional;

public class CatService {
	private static final String CAT_API_URL = "https://api.thecatapi.com/v1/images/search";

	public Optional<String> getRandomCatImage() {
		JSONArray response = HttpUtil.getJsonArray(CAT_API_URL);
		if ((response == null) || (response.isEmpty())) {
			return Optional.empty();
		}

		return Optional.of(response.getJSONObject(0).getString("url"));
	}
}
