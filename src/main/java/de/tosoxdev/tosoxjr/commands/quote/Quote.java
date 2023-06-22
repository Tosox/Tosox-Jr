package de.tosoxdev.tosoxjr.commands.quote;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class Quote {
    public static String getBreakingBad() {
        JSONObject response = APIRequest.getJson("https://api.breakingbadquotes.xyz/v1/quotes");
        if (response == null) {
            return null;
        }

        String quote = response.getString("quote");
        String author = response.getString("author");
        return String.format("_%s_\n~ %s", quote, author);
    }

    public static String getJoke() {
        JSONObject response = APIRequest.getJson("https://official-joke-api.appspot.com/jokes/random");
        if (response == null) {
            return null;
        }

        String setup = response.getString("setup");
        String punchline = response.getString("punchline");
        return String.format("%s\n... %s", setup, punchline);
    }

    public static String getFamous() {
        JSONObject response = APIRequest.getJson("https://api.quotable.io/quotes/random?tags=famous-quotes");
        if (response == null) {
            return null;
        }

        String content = response.getString("content");
        String author = response.getString("author");
        return String.format("_%s_\n~ %s", content, author);
    }

    public static String getWisdom() {
        JSONObject response = APIRequest.getJson("https://api.quotable.io/quotes/random?tags=wisdom");
        if (response == null) {
            return null;
        }

        String content = response.getString("content");
        String author = response.getString("author");
        return String.format("_%s_\n~ %s", content, author);
    }

    public static String getInspirational() {
        JSONObject response = APIRequest.getJson("https://zenquotes.io/api/random");
        if (response == null) {
            return null;
        }

        String q = response.getString("q");
        String a = response.getString("a");
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
