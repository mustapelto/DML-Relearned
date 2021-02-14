package mustapelto.deepmoblearning.common.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;

public class JsonHelper {
    public static String getString(JsonObject data, String key, String defaultValue) {
        String result = defaultValue;
        if (data.has(key)) {
            try {
                result = data.get(key).getAsString();
            } catch (UnsupportedOperationException e) {
                DMLRelearned.logger.warn("Invalid JSON entry: not a string! Using default value. Key: {}", key);
            }
        }
        return result;
    }

    public static String getString(JsonObject data, String key) {
        return getString(data, key, "");
    }

    public static int getInt(JsonObject data, String key, int defaultValue, int min, int max) {
        int result = defaultValue;
        if (data.has(key)) {
            try {
                result = data.get(key).getAsInt();
            } catch (UnsupportedOperationException e) {
                DMLRelearned.logger.warn("Invalid JSON entry: not an int! Using default value. Key: {}", key);
            }
        }
        return MathHelper.Clamp(result, min, max);
    }

    public static int getInt(JsonObject data, String key, int defaultValue) {
        return getInt(data, key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static int getInt(JsonObject data, String key) {
        return getInt(data, key, 0);
    }

    public static boolean getBoolean(JsonObject data, String key, boolean defaultValue) {
        boolean result = defaultValue;
        if (data.has(key)) {
            try {
                result = data.get(key).getAsBoolean();
            } catch (UnsupportedOperationException e) {
                DMLRelearned.logger.warn("Invalid JSON entry: not a boolean! Using default value. Key: {}", key);
            }
        }
        return result;
    }

    public static boolean getBoolean(JsonObject data, String key) {
        return getBoolean(data, key, false);
    }

    public static ImmutableList<String> getStringListFromJsonArray(JsonObject data, String key, String defaultValue) {
        JsonArray array = getJsonArray(data, key);
        if (array.size() == 0) {
            if (defaultValue.isEmpty())
                return ImmutableList.of();
            else
                return ImmutableList.of(defaultValue);
        }

        return toStringList(array);
    }

    public static ImmutableList<String> getStringListFromJsonArray(JsonObject data, String key) {
        return getStringListFromJsonArray(data, key, "");
    }

    public static JsonArray getJsonArray(JsonObject data, String key) {
        JsonArray result = new JsonArray();
        if (data.has(key)) {
            try {
                result = data.get(key).getAsJsonArray();
            } catch (IllegalStateException e) {
                DMLRelearned.logger.warn("Invalid JSON entry: not an array! Using default value. Key: {}", key);
            }
        }
        return result;
    }

    public static JsonObject getJsonObject(JsonObject data, String key) {
        JsonObject result = new JsonObject();
        if (data.has(key)) {
            try {
                result = data.get(key).getAsJsonObject();
            } catch (IllegalStateException e) {
                DMLRelearned.logger.warn("Invalid JSON entry: not an object! Using default values. Key: {}", key);
            }
        }
        return result;
    }

    private static ImmutableList<String> toStringList(JsonArray data) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (JsonElement element : data) {
            try {
                builder.add(element.getAsString());
            } catch (UnsupportedOperationException e) {
                DMLRelearned.logger.warn("Invalid entry in JSON array: not a string! Skipping.");
            }
        }
        return builder.build();
    }

    private static boolean isValidRegistryName(String registryName) {
        return registryName.contains(":");
    }

    public static String getRegistryName(JsonObject data, String key, String defaultValue) {
        String jsonEntry = getString(data, key);
        if (!jsonEntry.isEmpty() && !isValidRegistryName(jsonEntry)) {
            DMLRelearned.logger.warn("Invalid registry name entry in JSON! Using default value (if applicable).");
            return defaultValue;
        }
        return jsonEntry;
    }

    public static String getRegistryName(JsonObject data, String key) {
        return getRegistryName(data, key, "");
    }
}
