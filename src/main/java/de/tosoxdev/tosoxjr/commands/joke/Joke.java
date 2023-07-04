package de.tosoxdev.tosoxjr.commands.joke;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import org.json.JSONObject;

public class Joke {
    private final static String JOKE_TEMPLATE = "%s\n... %s";

    public static String getGeneral() {
        JSONObject response = (JSONObject) APIRequest.getJson("https://official-joke-api.appspot.com/jokes/general/random");
        if (response == null) {
            return null;
        }

        String setup = response.getString("setup");
        String punchline = response.getString("punchline");
        return String.format(JOKE_TEMPLATE, setup, punchline);
    }

    public static String getProgramming() {
        JSONObject response = (JSONObject) APIRequest.getJson("https://official-joke-api.appspot.com/jokes/programming/random");
        if (response == null) {
            return null;
        }

        String setup = response.getString("setup");
        String punchline = response.getString("punchline");
        return String.format(JOKE_TEMPLATE, setup, punchline);
    }
}
