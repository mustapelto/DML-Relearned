package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.MathHelper;
import net.minecraftforge.fml.common.Loader;

public abstract class MetaDataBase {
    protected String modID; // Mod ID the item is related to. "minecraft" for vanilla-related items.
    protected String itemID; // Item ID. Used for model/texture assignment.

    protected void validate(JsonObject data, String[] requiredFields, String dataType) {
        StringBuilder missingItems = new StringBuilder();
        for (int i = 0; i < requiredFields.length; i++) {
            missingItems.append(getMissing(data, requiredFields[i], i == 0));
        }
        if (missingItems.length() > 0) {
            DMLRelearned.logger.error(String.format("Config Error: %s entry is missing fields:\n%s", dataType, missingItems.toString()));
            throw new IllegalArgumentException();
        }
    }

    private String getMissing(JsonObject data, String key, boolean isFirstItem) {
        return data.has(key) ? "" : (isFirstItem ? "" : "\n") + key;
    }

    public String getModID() {
        return modID;
    }

    public String getItemID() {
        return itemID;
    }

    public boolean isModLoaded() {
        return modID.equals(DMLConstants.MINECRAFT) || Loader.isModLoaded(modID);
    }

    // Methods used for instance initialization / reading and writing JSON

    /**
     * Returns metadata as JSON object
     * Does not include mod ID, which is used as a category name in the JSON file
     *
     * @return JSON object containing metadata
     */
    public abstract JsonObject toJsonObject();

    protected static String getOrDefault(JsonObject data, String key, String defaultValue) {
        return data.has(key) ? data.get(key).getAsString() : defaultValue;
    }

    protected static int getOrDefault(JsonObject data, String key, int defaultValue) {
        return data.has(key) ? data.get(key).getAsInt() : defaultValue;
    }

    protected static int getOrDefault(JsonObject data, String key, int defaultValue, int min, int max) {
        int value = data.has(key) ? data.get(key).getAsInt() : defaultValue;
        return MathHelper.Clamp(value, min, max);
    }

    protected static boolean getOrDefault(JsonObject data, String key, boolean defaultValue) {
        return data.has(key) ? data.get(key).getAsBoolean() : defaultValue;
    }

    protected static String[] getOrDefault(JsonObject data, String key, String[] defaultValue) {
        return data.has(key) ? jsonArrayToStringArray(data.get(key).getAsJsonArray()) : defaultValue;
    }

    protected static String[] jsonArrayToStringArray(JsonArray input) {
        String[] result = new String[input.size()];

        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).getAsString();
        }

        return result;
    }

    protected static JsonArray stringArrayToJsonArray(String[] list) {
        JsonArray result = new JsonArray();
        for (String item : list) {
            result.add(item);
        }
        return result;
    }
}
