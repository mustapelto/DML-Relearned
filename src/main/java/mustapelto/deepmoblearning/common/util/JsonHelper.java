package mustapelto.deepmoblearning.common.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;

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

    public static ImmutableList<String> getJsonArrayAsStringList(JsonObject data, String key) {
        return (data.has(key)) ?
                jsonArrayToStringList(data.getAsJsonArray(key)) :
                ImmutableList.of();
    }

    public static ImmutableList<String> getJsonArrayAsStringList(JsonObject data, String key, String defaultValue) {
        return (data.has(key)) ?
                jsonArrayToStringList(data.getAsJsonArray(key)) :
                ImmutableList.of(defaultValue);
    }

    private static ImmutableList<String> jsonArrayToStringList(JsonArray array) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (JsonElement element : array) {
            try {
                builder.add(element.getAsString());
            } catch (UnsupportedOperationException e) {
                DMLRelearned.logger.warn("Invalid entry in JSON string array");
            }
        }
        return builder.build();
    }
}
