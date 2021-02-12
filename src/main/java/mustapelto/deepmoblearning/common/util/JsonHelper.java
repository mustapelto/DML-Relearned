package mustapelto.deepmoblearning.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonHelper {
    public static String getOrDefault(JsonObject data, String key, String defaultValue) {
        return data.has(key) ? data.get(key).getAsString() : defaultValue;
    }

    public static int getOrDefault(JsonObject data, String key, int defaultValue) {
        return data.has(key) ? data.get(key).getAsInt() : defaultValue;
    }

    public static int getOrDefault(JsonObject data, String key, int defaultValue, int min, int max) {
        int value = data.has(key) ? data.get(key).getAsInt() : defaultValue;
        return MathHelper.Clamp(value, min, max);
    }

    public static boolean getOrDefault(JsonObject data, String key, boolean defaultValue) {
        return data.has(key) ? data.get(key).getAsBoolean() : defaultValue;
    }

    public static String[] getOrDefault(JsonObject data, String key, String[] defaultValue) {
        return data.has(key) ? jsonArrayToStringArray(data.get(key).getAsJsonArray()) : defaultValue;
    }

    public static JsonArray getJsonArray(JsonObject data, String key) {
        return data.has(key) ? data.getAsJsonArray(key) : new JsonArray();
    }

    public static String[] jsonArrayToStringArray(JsonArray input) {
        String[] result = new String[input.size()];

        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).getAsString();
        }

        return result;
    }
}
