package de.tosox.tosoxjr.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class HttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux i686; rv:57.0) Gecko/20100101 Firefox/57.0";

    public static String getString(String url) {
        return sendRequest(url)
                .orElse(null);
    }

    public static JSONObject getJsonObject(String url) {
        return sendRequest(url)
                .map(JSONObject::new)
                .orElse(null);
    }

    public static JSONArray getJsonArray(String url) {
        return sendRequest(url)
                .map(JSONArray::new)
                .orElse(null);
    }

    private static Optional<String> sendRequest(String url) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", USER_AGENT);

        try (Response response = CLIENT.newCall(requestBuilder.build()).execute()) {
            ResponseBody body = response.body();
            if (body != null) {
                LOGGER.info("Response body: {}", body.string());
                return Optional.of(body.string());
            }
        } catch (IOException e) {
            LOGGER.error("HTTP request failed for {}: {}", url, e.getMessage());
        }
        return Optional.empty();
    }
}
