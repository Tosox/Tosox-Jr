package de.tosox.tosoxjr.service;

import de.tosox.tosoxjr.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

public class QuoteService {
	private final static String QUOTE_FORMAT_TEMPLATE = "_%s_\n~ %s";
	private static final String BREAKING_BAD_API = "https://api.breakingbadquotes.xyz/v1/quotes";
	private static final String FAMOUS_API = "https://quoterism.com/api/quotes/random";
	private static final String INSPIRATIONAL_API = "https://zenquotes.io/api/random";

	public Optional<String> getBreakingBad() {
		JSONArray response = HttpUtil.getJsonArray(BREAKING_BAD_API);
		if ((response == null) || (response.isEmpty())) {
			return Optional.empty();
		}

		String quote = response.getJSONObject(0).getString("quote");
		String author = response.getJSONObject(0).getString("author");
		return Optional.of(String.format(QUOTE_FORMAT_TEMPLATE, quote, author));
	}

	public Optional<String> getFamous() {
		JSONObject response = HttpUtil.getJsonObject(FAMOUS_API);
		if (response == null) {
			return Optional.empty();
		}

		String quote = response.getString("text");
		String author = response.getJSONObject("author").getString("name");
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
