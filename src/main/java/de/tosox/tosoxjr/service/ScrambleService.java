package de.tosox.tosoxjr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ScrambleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScrambleService.class);

	private static final Map<String, String> WORD_LIST_RESOURCES = Map.of(
			"en", "en-nouns.txt",
			"de", "de-nouns.txt"
	);

	private final Map<String, List<String>> cachedWordLists = new HashMap<>();

	private List<String> loadWordList(String lang) {
		String resource = WORD_LIST_RESOURCES.getOrDefault(lang, WORD_LIST_RESOURCES.get("en"));
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(resource)) {
			if (is != null) {
				String content = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
				return List.of(content.split(","));
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load word list '{}': {}", resource, e.getMessage());
		}
		return Collections.emptyList();
	}

	public Optional<String> getRandomWord(String language) {
		List<String> words = cachedWordLists.computeIfAbsent(language.toLowerCase(), this::loadWordList);
		if (words.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(words.get(ThreadLocalRandom.current().nextInt(words.size())));
	}

	public Set<String> getAvailableLanguages() {
		return WORD_LIST_RESOURCES.keySet();
	}
}
