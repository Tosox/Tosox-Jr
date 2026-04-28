package de.tosox.tosoxjr.service;

import de.tosox.tosoxjr.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteService {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuoteService.class);
	private final static String QUOTE_FORMAT_TEMPLATE = "_%s_\n~ %s";
	private static final String MOTIVATIONAL_API = "https://motivational-spark-api.vercel.app/api/quotes/random";
	private static final String INSPIRATIONAL_API = "https://zenquotes.io/api/random";

	private final JSONArray breakingBadQuotes;

	public QuoteService() {
		JSONArray loaded = null;
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("breaking_bad_quotes.json")) {
			if (is != null) {
				loaded = new JSONArray(new String(is.readAllBytes(), StandardCharsets.UTF_8));
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load breaking_bad_quotes.json: {}", e.getMessage());
		}
		breakingBadQuotes = loaded;
	}

	public Optional<String> getBreakingBad() {
		if (breakingBadQuotes == null || breakingBadQuotes.isEmpty()) {
			return Optional.empty();
		}

		JSONObject entry = breakingBadQuotes.getJSONObject(
				ThreadLocalRandom.current().nextInt(breakingBadQuotes.length()));
		String quote = entry.getString("quote");
		String author = entry.getString("author");
		return Optional.of(String.format(QUOTE_FORMAT_TEMPLATE, quote, author));
	}

	public Optional<String> getMotivational() {
		JSONObject response = HttpUtil.getJsonObject(MOTIVATIONAL_API);
		if (response == null) {
			return Optional.empty();
		}

		String quote = response.getString("quote");
		String author = response.getString("author");
		return Optional.of(String.format(QUOTE_FORMAT_TEMPLATE, quote, author));
	}

	public Optional<String> getInspirational() {
		JSONArray response = HttpUtil.getJsonArray(INSPIRATIONAL_API);
		if ((response == null) || (response.isEmpty())) {
			return Optional.empty();
		}

		String quote = response.getJSONObject(0).getString("q");
		String author = response.getJSONObject(0).getString("a");
		return Optional.of(String.format(QUOTE_FORMAT_TEMPLATE, quote, author));
	}
}
