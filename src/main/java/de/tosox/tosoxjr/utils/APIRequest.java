package de.tosox.tosoxjr.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIRequest.class);

    private static HttpResponse<String> get(String query) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(query))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            LOGGER.error("The response for request '{}' was invalid", query);
            return null;
        }

        return response;
    }

    public static String getString(String query) {
        HttpResponse<String> response = get(query);
        if (response == null) {
            return null;
        }

        return response.body();
    }

    public static Object getJson(String query) {
        HttpResponse<String> response = get(query);
        if (response == null) {
            return null;
        }

        String body = response.body();
        if (body == null) {
            return null;
        }

        char fistChar = body.charAt(0);
        if (fistChar == '{') {
            return new JSONObject(body);
        } else if (fistChar == '[')
            return new JSONArray(body);
        else {
            LOGGER.error("The json body for request '{}' is malformed", query);
            return null;
        }
    }
}
