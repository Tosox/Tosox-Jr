package de.tosox.tosoxjr.service;

import de.tosox.tosoxjr.util.HttpUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ScrambleService {
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
		return Optional.of(words.get(randomIdx));
	}

	public Set<String> getAvailableLanguages() {
		return RANDOM_WORD_APIS.keySet();
	}
}
