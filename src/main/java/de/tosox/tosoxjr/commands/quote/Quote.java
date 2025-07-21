package de.tosox.tosoxjr.commands.quote;

import de.tosox.tosoxjr.utils.APIRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class Quote {
    private final static String QUOTE_TEMPLATE = "_%s_\n~ %s";

    public static String getBreakingBad() {
        JSONArray response = (JSONArray) APIRequest.getJson("https://api.breakingbadquotes.xyz/v1/quotes");
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        String quote = response.getJSONObject(0).getString("quote");
        String author = response.getJSONObject(0).getString("author");
        return String.format(QUOTE_TEMPLATE, quote, author);
    }

    public static String getFamous() {
        JSONObject response = (JSONObject) APIRequest.getJson("https://quoterism.com/api/quotes/random");
        if (response == null) {
            return null;
        }

        String content = response.getString("text");
        String author = ((JSONObject) response.get("author")).getString("name");
        return String.format(QUOTE_TEMPLATE, content, author);
    }

    public static String getInspirational() {
        JSONArray response = (JSONArray) APIRequest.getJson("https://zenquotes.io/api/random");
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        String q = response.getJSONObject(0).getString("q");
        String a = response.getJSONObject(0).getString("a");
        return String.format(QUOTE_TEMPLATE, q, a);
    }
}
