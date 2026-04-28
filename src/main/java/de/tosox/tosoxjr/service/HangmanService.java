package de.tosox.tosoxjr.service;

import de.tosox.tosoxjr.util.Constants;
import de.tosox.tosoxjr.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class HangmanService {
	private static final Map<String, String> RANDOM_WORD_APIS = Map.of(
			"en", "https://capitalizemytitle.com/wp-content/tools/random-word/en/nouns.txt",
			"de", "https://capitalizemytitle.com/wp-content/tools/random-word/de/nouns.txt"
	);

	private final Map<String, List<String>> cachedWordLists = new HashMap<>();

	private List<String> loadWordList(String lang) {
		String url = RANDOM_WORD_APIS.getOrDefault(lang, RANDOM_WORD_APIS.get("en"));
		String response = HttpUtil.getString(url);
		if (response != null) {
			return List.of(response.split(","));
		}
		return Collections.emptyList();
	}

	public Optional<String> getRandomWord(String language) {
		List<String> words = cachedWordLists.computeIfAbsent(language.toLowerCase(), this::loadWordList);
		if (words.isEmpty()) {
			return Optional.empty();
		}

		int randomIdx = ThreadLocalRandom.current().nextInt(words.size());
		return Optional.of(words.get(randomIdx).toUpperCase());
	}

	public Optional<String> getDefinition(String word) {
		String url = String.format(
				"https://www.dictionaryapi.com/api/v3/references/collegiate/json/%s?key=%s",
				word, Constants.DICTIONARY_API_KEY
		);
		JSONArray response = HttpUtil.getJsonArray(url);
		if (response == null || response.isEmpty()) {
			return Optional.empty();
		}

		try {
			JSONObject obj = response.getJSONObject(0);
			JSONArray shortDefs = obj.optJSONArray("shortdef");
			if (shortDefs == null || shortDefs.isEmpty()) {
				return Optional.empty();
			}

			int randomDef = ThreadLocalRandom.current().nextInt(shortDefs.length());
			return Optional.of(shortDefs.getString(randomDef));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public Set<String> getAvailableLanguages() {
		return RANDOM_WORD_APIS.keySet();
	}
}
