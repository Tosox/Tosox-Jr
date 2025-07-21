package de.tosox.tosoxjr.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class APIRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIRequest.class);

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private static String get(String query) {
        Request request = new Request.Builder()
                .url(query)
                .get()
                .header("User-Agent", "Mozilla/5.0 (X11; Linux i686; rv:57.0) Gecko/20100101 Firefox/57.0")
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                LOGGER.error("Response body for request '{}' is null", query);
                return null;
            }

            return body.string();
        } catch (IOException e) {
            LOGGER.error("The response for request '{}' was invalid", query);
            return null;
        }
    }

    public static String getString(String query) {
        return get(query);
    }

    public static Object getJson(String query) {
        String body = getString(query);
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
