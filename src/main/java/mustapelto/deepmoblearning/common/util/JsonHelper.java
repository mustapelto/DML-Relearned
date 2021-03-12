package mustapelto.deepmoblearning.common.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

/**
 * Helper methods for reading JSON data
 */
public class JsonHelper {
    public static String getString(JsonObject data, String key, String defaultValue) {
        if (!data.has(key))
            return defaultValue;

        JsonElement element = data.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            DMLRelearned.logger.warn("Invalid JSON entry with key \"{}\": not a string! Using default value", key);
            return defaultValue;
        }

        return element.getAsString();
    }

    public static String getString(JsonObject data, String key) {
        return getString(data, key, "");
    }

    public static int getInt(JsonObject data, String key, int defaultValue, int min, int max) {
        if (!data.has(key))
            return defaultValue;

        JsonElement element = data.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            DMLRelearned.logger.warn("Invalid JSON entry with key \"{}\": not a number! Using default value", key);
            return defaultValue;
        }

        return MathHelper.clamp(element.getAsInt(), min, max);
    }

    public static double getDouble(JsonObject data, String key, double defaultValue, double min, double max) {
        if (!data.has(key))
            return defaultValue;

        JsonElement element = data.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            DMLRelearned.logger.warn("Invalid JSON entry with key \"{}\": not a number! Using default value", key);
            return defaultValue;
        }

        return MathHelper.clamp(element.getAsDouble(), min, max);
    }

    public static boolean getBoolean(JsonObject data, String key, boolean defaultValue) {
        if (!data.has(key))
            return defaultValue;

        JsonElement element = data.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
            DMLRelearned.logger.warn("Invalid JSON entry with key \"{}\": not a boolean! Using default value", key);
            return defaultValue;
        }

        return element.getAsBoolean();
    }

    public static boolean getBoolean(JsonObject data, String key) {
        return getBoolean(data, key, false);
    }

    public static ImmutableList<String> getStringListFromJsonArray(JsonObject data, String key, String defaultValue) {
        if (!data.has(key))
            return getDefaultStringList(defaultValue);

        JsonElement element = data.get(key);
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            if (array.size() == 0)
                return getDefaultStringList(defaultValue);

            return toStringList(array);
        }

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (!primitive.isString())
                return getDefaultStringList(defaultValue);

            return ImmutableList.of(primitive.getAsString());
        }

        return getDefaultStringList(defaultValue);
    }

    public static ImmutableList<String> getStringListFromJsonArray(JsonObject data, String key) {
        return getStringListFromJsonArray(data, key, "");
    }

    public static ImmutableList<String> getDefaultStringList(String defaultValue) {
        return defaultValue.isEmpty() ? ImmutableList.of() : ImmutableList.of(defaultValue);
    }

    public static JsonObject getJsonObject(JsonObject data, String key) {
        if (!data.has(key)) {
            DMLRelearned.logger.warn(getMissingJsonWarning(true, key));
            return new JsonObject();
        }

        JsonElement element = data.get(key);
        if (!element.isJsonObject()) {
            DMLRelearned.logger.warn(getInvalidJsonWarning(JsonElementType.OBJECT, true, key));
            return new JsonObject();
        }

        return element.getAsJsonObject();
    }

    private static ImmutableList<String> toStringList(JsonArray data) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (JsonElement element : data) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                DMLRelearned.logger.warn("Invalid entry in JSON array: not a string! Skipping.");
                continue;
            }

            builder.add(element.getAsString());
        }

        return builder.build();
    }

    public static ImmutableList<WeightedItem<String>> getWeightedStringList(JsonObject data, String key) {
        if (!data.has(key))
            return ImmutableList.of();

        JsonElement listElement = data.get(key);
        if (listElement.isJsonArray()) {
            JsonArray array = listElement.getAsJsonArray();

            if (array.size() == 0)
                return ImmutableList.of();

            ImmutableList.Builder<WeightedItem<String>> builder = ImmutableList.builder();

            for (JsonElement itemElement : array) {
                if (!itemElement.isJsonArray()) {
                    DMLRelearned.logger.warn("Invalid weighted item entry, skipping!");
                    continue;
                }

                Optional<WeightedItem<String>> weightedItem = getWeightedItem(itemElement.getAsJsonArray());
                weightedItem.ifPresent(builder::add);
            }

            return builder.build();
        }

        if (listElement.isJsonPrimitive()) {
            JsonPrimitive primitive = listElement.getAsJsonPrimitive();
            if (!primitive.isString())
                return ImmutableList.of();

            return ImmutableList.of(new WeightedItem<>(primitive.getAsString(), 100));
        }

        return ImmutableList.of();
    }

    private static Optional<WeightedItem<String>> getWeightedItem(JsonArray itemJson) {
        if (itemJson.size() == 0 || itemJson.size() > 2) {
            DMLRelearned.logger.warn("Invalid weighted item entry, skipping!");
            return Optional.empty();
        }

        JsonElement valueElement = itemJson.get(0);

        if (!valueElement.isJsonPrimitive() || !valueElement.getAsJsonPrimitive().isString()) {
            DMLRelearned.logger.warn("Invalid weighted item entry, skipping!");
            return Optional.empty();
        }

        String value = valueElement.getAsString();
        int weight = 100;

        if (itemJson.size() == 2) {
            JsonElement weightElement = itemJson.get(1);

            if (!weightElement.isJsonPrimitive() || !weightElement.getAsJsonPrimitive().isNumber()) {
                DMLRelearned.logger.warn("Invalid weighted item weight, using default!");
            } else {
                weight = weightElement.getAsInt();
            }
        }

        return Optional.of(new WeightedItem<>(value, weight));
    }

    private static boolean isValidRegistryName(String registryName) {
        return registryName.contains(":");
    }

    public static ResourceLocation getResourceLocation(JsonObject data, String key, String defaultValue) {
        String jsonEntry = getString(data, key);
        if (!jsonEntry.isEmpty() && !isValidRegistryName(jsonEntry)) {
            DMLRelearned.logger.warn("Invalid registry name entry in JSON! Using default value (if applicable).");
            return new ResourceLocation(defaultValue);
        }
        return new ResourceLocation(jsonEntry);
    }

    public static ResourceLocation getResourceLocation(JsonObject data, String key) {
        return getResourceLocation(data, key, "");
    }

    private static String getInvalidJsonWarning(JsonElementType elementType, boolean usingDefault, String key) {
        String elementString = "";
        switch (elementType) {
            case OBJECT: elementString = "object"; break;
            case ARRAY: elementString = "array"; break;
        }
        String action = usingDefault ? "Using default values." : "Skipping.";
        return String.format("Invalid JSON entry with key \"%s\": not an %s! %s.", key, elementString, action);
    }

    private static String getMissingJsonWarning(boolean usingDefault, String key) {
        String action = usingDefault ? "Using default values." : "Skipping.";
        return String.format("Could not find JSON entry with key \"%s\". %s", key, action);
    }

    private enum JsonElementType { OBJECT, ARRAY }
}
