package de.tosoxdev.tosoxjr.commands.quote;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class Quote {
    public static String getBreakingBad() {
        JSONArray response = (JSONArray) APIRequest.getJson("https://api.breakingbadquotes.xyz/v1/quotes");
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        String quote = response.getJSONObject(0).getString("quote");
        String author = response.getJSONObject(0).getString("author");
        return String.format("_%s_\n~ %s", quote, author);
    }

    public static String getJoke() {
        JSONObject response = (JSONObject) APIRequest.getJson("https://official-joke-api.appspot.com/jokes/random");
        if (response == null) {
            return null;
        }

        String setup = response.getString("setup");
        String punchline = response.getString("punchline");
        return String.format("%s\n... %s", setup, punchline);
    }

    public static String getFamous() {
        JSONArray response = (JSONArray) APIRequest.getJson("https://api.quotable.io/quotes/random?tags=famous-quotes");
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        String content = response.getJSONObject(0).getString("content");
        String author = response.getJSONObject(0).getString("author");
        return String.format("_%s_\n~ %s", content, author);
    }

    public static String getWisdom() {
        JSONArray response = (JSONArray) APIRequest.getJson("https://api.quotable.io/quotes/random?tags=wisdom");
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        String content = response.getJSONObject(0).getString("content");
        String author = response.getJSONObject(0).getString("author");
        return String.format("_%s_\n~ %s", content, author);
    }

    public static String getInspirational() {
        JSONArray response = (JSONArray) APIRequest.getJson("https://zenquotes.io/api/random");
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        String q = response.getJSONObject(0).getString("q");
        String a = response.getJSONObject(0).getString("a");
        return String.format("_%s_\n~ %s", q, a);
    }

    public static String getFromCallable(Callable<String> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            return null;
        }
    }
}
