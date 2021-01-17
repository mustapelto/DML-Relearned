package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class LivingMatterData extends MetaDataBase {
    private final String displayName;
    private final String displayNameColor;
    private final int xpValue;

    public LivingMatterData(JsonObject data) {
        validate(data, new String[]{"itemID"}, "LivingMatterData");

        itemID = getOrDefault(data, "itemID", "");
        modID = getOrDefault(data, "modID", DMLConstants.MINECRAFT);
        displayName = getOrDefault(data, "displayName", "");
        displayNameColor = getOrDefault(data, "displayNameColor", "white");
        xpValue = getOrDefault(data, "xpValue", 0);
    }

    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public TextFormatting getDisplayNameColor() {
        return TextFormatting.getValueByName(displayNameColor);
    }

    public int getXpValue() {
        return xpValue;
    }

    public static JsonObject createJsonObject(String itemID, String modID, String displayName, String displayNameColor, int xpValue) {
        JsonObject object = new JsonObject();

        object.addProperty("itemID", itemID);
        if (!modID.equals(DMLConstants.MINECRAFT))
            object.addProperty("modID", modID);
        object.addProperty("displayName", displayName);
        object.addProperty("displayNameColor", displayNameColor);
        object.addProperty("xpValue", xpValue);

        return object;
    }
}
