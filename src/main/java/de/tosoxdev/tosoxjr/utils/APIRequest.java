package de.tosoxdev.tosoxjr.utils;

import de.tosoxdev.tosoxjr.Main;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIRequest {
    private static HttpResponse<String> get(String query) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(query))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Main.LOGGER.error("The response for request '{}' was invalid", query);
            return null;
        }

        return response;
    }

    public static JSONObject getJson(String query) {
        HttpResponse<String> response = get(query);
        if (response == null) {
            return null;
        }

        String body = response.body();
        if (body == null) {
            return null;
        }

        body = body.replaceAll("^\\[(.+)]$", "$1"); // Remove outer brackets if existing

        try {
            return new JSONObject(body);
        } catch (JSONException e) {
            Main.LOGGER.error("The json body for request '{}' is malformed", query);
            return null;
        }
    }
}
