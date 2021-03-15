package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.DMLRHelper;
import mustapelto.deepmoblearning.common.util.MathHelper;
import mustapelto.deepmoblearning.common.util.WeightedString;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public abstract class Metadata {
    public abstract void finalizeData();
    public abstract String getID();

    protected static boolean isInvalidJson(JsonObject data, String[] requiredKeys) {
        for (String key : requiredKeys) {
            if (!data.has(key))
                return true;
        }
        return false;
    }

    protected static Optional<String> getString(JsonObject data, String key) {
        if (!data.has(key))
            return Optional.empty();

        JsonElement element = data.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            DMLRelearned.logger.warn(getInvalidString(key));
            return Optional.empty();
        }

        return Optional.of(element.getAsString());
    }

    private static Optional<JsonPrimitive> getNumber(JsonObject data, String key) {
        if (!data.has(key))
            return Optional.empty();

        JsonElement element = data.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            DMLRelearned.logger.warn(getInvalidString(key));
            return Optional.empty();
        }

        return Optional.of(element.getAsJsonPrimitive());
    }

    protected static Optional<Integer> getInt(JsonObject data, String key, int min, int max) {
        return getNumber(data, key)
                .map(number -> MathHelper.clamp(number.getAsInt(), min, max));
    }

    protected static Optional<Double> getDouble(JsonObject data, String key, double min, double max) {
        return getNumber(data, key)
                .map(number -> MathHelper.clamp(number.getAsDouble(), min, max));
    }

    protected static Optional<Boolean> getBoolean(JsonObject data, String key) {
        if (!data.has(key))
            return Optional.empty();

        JsonElement element = data.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
            DMLRelearned.logger.warn(getInvalidString(key));
            return Optional.empty();
        }

        return Optional.of(element.getAsBoolean());
    }

    protected static Optional<ImmutableList<String>> getStringList(JsonObject data, String key) {
        if (!data.has(key))
            return Optional.empty();

        JsonElement element = data.get(key);
        if (!element.isJsonArray()) {
            DMLRelearned.logger.warn(getInvalidString(key));
            return Optional.empty();
        }

        JsonArray array = element.getAsJsonArray();
        if (array.size() == 0)
            return Optional.of(ImmutableList.of());

        ImmutableList<String> stringList = getStringList(array);
        if (stringList.isEmpty())
            return Optional.of(ImmutableList.of());
        else
            return Optional.of(stringList);
    }

    private static ImmutableList<String> getStringList(JsonArray data) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (JsonElement element : data) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                DMLRelearned.logger.warn("Invalid entry in JSON array: not a string!");
                continue;
            }

            builder.add(element.getAsString());
        }

        return builder.build();
    }

    protected static Optional<JsonObject> getJsonObject(JsonObject data, String key) {
        if (!data.has(key)) {
            DMLRelearned.logger.warn("Missing JSON entry with key \"{}\"!", key);
            return Optional.empty();
        }

        JsonElement element = data.get(key);
        if (!element.isJsonObject()) {
            DMLRelearned.logger.warn(getInvalidString(key));
            return Optional.empty();
        }

        return Optional.of(element.getAsJsonObject());
    }

    protected static Optional<ImmutableList<WeightedString>> getWeightedStringList(JsonObject data, String key) {
        if (!data.has(key))
            return Optional.empty();

        JsonElement listElement = data.get(key);
        if (listElement.isJsonArray()) {
            JsonArray array = listElement.getAsJsonArray();

            if (array.size() == 0)
                return Optional.of(ImmutableList.of());

            ImmutableList.Builder<WeightedString> builder = ImmutableList.builder();

            for (JsonElement itemElement : array) {
                if (!itemElement.isJsonArray()) {
                    DMLRelearned.logger.warn("Invalid weighted item entry, skipping!");
                    continue;
                }

                Optional<WeightedString> weightedItem = getWeightedItem(itemElement.getAsJsonArray());
                weightedItem.ifPresent(builder::add);
            }

            return Optional.of(builder.build());
        }

        if (listElement.isJsonPrimitive()) {
            JsonPrimitive primitive = listElement.getAsJsonPrimitive();
            if (!primitive.isString()) {
                DMLRelearned.logger.warn(getInvalidString(key));
                return Optional.empty();
            }

            return Optional.of(ImmutableList.of(new WeightedString(primitive.getAsString(), 100)));
        }

        DMLRelearned.logger.warn(getInvalidString(key));
        return Optional.empty();
    }

    private static Optional<WeightedString> getWeightedItem(JsonArray itemJson) {
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

        return Optional.of(new WeightedString(value, weight));
    }

    protected static Optional<ResourceLocation> getResourceLocation(JsonObject data, String key) {
        String jsonEntry = getString(data, key).orElse("");
        if (!jsonEntry.isEmpty() && !DMLRHelper.isValidRegistryString(jsonEntry)) {
            DMLRelearned.logger.warn("Invalid registry name entry in JSON! Using default value (if applicable).");
            return Optional.empty();
        }
        return Optional.of(new ResourceLocation(jsonEntry));
    }

    private static String getInvalidString(String key) {
        return String.format("Invalid JSON entry with key: %s", key);
    }
}
