package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;

public class LivingMatterData extends MetaDataBase {
    private final String displayName;
    private final String displayNameFormatted;
    private final int xpValue;

    public LivingMatterData(JsonObject data) {
        validate(data, new String[]{"itemID"}, "LivingMatterData");

        itemID = getOrDefault(data, "itemID", "");
        modID = getOrDefault(data, "modID", DMLConstants.MINECRAFT);
        displayName = getOrDefault(data, "displayName", "");
        displayNameFormatted = getOrDefault(data, "displayNameFormatted", "");
        xpValue = getOrDefault(data, "xpValue", 0);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameFormatted() {
        return displayNameFormatted.equals("") ? displayName : displayNameFormatted;
    }

    public int getXpValue() {
        return xpValue;
    }

    public static JsonObject createJsonObject(String itemID, String modID, String displayName, String displayNameFormatted, int xpValue) {
        JsonObject object = new JsonObject();

        object.addProperty("itemID", itemID);
        if (!modID.equals(DMLConstants.MINECRAFT))
            object.addProperty("modID", modID);
        object.addProperty("displayName", displayName);
        object.addProperty("displayNameFormatted", displayNameFormatted);
        object.addProperty("xpValue", xpValue);

        return object;
    }
}