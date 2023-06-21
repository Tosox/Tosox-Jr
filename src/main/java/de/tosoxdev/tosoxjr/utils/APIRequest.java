package de.tosoxdev.tosoxjr.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIRequest {
    public static JSONObject get(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.printf("[ERROR]: The response for request '%s' was invalid\n", url);
            return null;
        }

        String body = response.body();
        body = body.replaceAll("^\\[(.+)]$", "$1"); // Remove outer brackets if existing

        try {
            return new JSONObject(body);
        } catch (JSONException e) {
            System.out.printf("[ERROR]: The json body for request '%s' is invalid\n", url);
            return null;
        }
    }
}
