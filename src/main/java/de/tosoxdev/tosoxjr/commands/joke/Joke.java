package de.tosoxdev.tosoxjr.commands.joke;

import de.tosoxdev.tosoxjr.utils.APIRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class Joke {
    private final static String PUN_TEMPLATE = "%s\n... %s";

    public static String getGeneral() {
        JSONArray response = (JSONArray) APIRequest.getJson("https://official-joke-api.appspot.com/jokes/general/random");
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        String setup = response.getJSONObject(0).getString("setup");
        String punchline = response.getJSONObject(0).getString("punchline");
        return String.format(PUN_TEMPLATE, setup, punchline);
    }

    public static String getProgramming() {
        JSONArray response = (JSONArray) APIRequest.getJson("https://official-joke-api.appspot.com/jokes/programming/random");
        if ((response == null) || (response.isEmpty())) {
            return null;
        }

        String setup = response.getJSONObject(0).getString("setup");
        String punchline = response.getJSONObject(0).getString("punchline");
        return String.format(PUN_TEMPLATE, setup, punchline);
    }

    public static String getChuckNorris() {
        JSONObject response = (JSONObject) APIRequest.getJson("https://api.chucknorris.io/jokes/random");
        if (response == null) {
            return null;
        }

        return response.getString("value");
    }
}
