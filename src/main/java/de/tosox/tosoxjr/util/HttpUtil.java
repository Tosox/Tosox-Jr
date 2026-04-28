package de.tosox.tosoxjr.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class HttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    public static String getString(String url) {
        return sendRequest(url)
                .orElse(null);
    }

    public static JSONObject getJsonObject(String url) {
        return sendRequest(url)
                .map(body -> {
                    try {
                        return new JSONObject(body);
                    } catch (JSONException e) {
                        LOGGER.error("Failed to parse JSON object from {}: {}", url, e.getMessage());
                        return null;
                    }
                })
                .orElse(null);
    }

    public static JSONArray getJsonArray(String url) {
        return sendRequest(url)
                .map(body -> {
                    try {
                        return new JSONArray(body);
                    } catch (JSONException e) {
                        LOGGER.error("Failed to parse JSON array from {}: {}", url, e.getMessage());
                        return null;
                    }
                })
                .orElse(null);
    }

    private static Optional<String> sendRequest(String url) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", USER_AGENT);

        try (Response response = CLIENT.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                LOGGER.error("HTTP request to {} returned status {}", url, response.code());
                return Optional.empty();
            }
            ResponseBody body = response.body();
            if (body != null) {
                return Optional.of(body.string());
            }
        } catch (IOException e) {
            LOGGER.error("HTTP request failed for {}: {}", url, e.getMessage());
        }
        return Optional.empty();
    }
}
