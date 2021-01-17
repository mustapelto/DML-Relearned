package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class LivingMatterData extends MetaDataBase {
    private final String displayName;
    private final String displayNameColor;
    private final int xpValue;

    public LivingMatterData(String modID, JsonObject data) {
        validate(data, new String[]{"itemID"}, "LivingMatterData");

        itemID = getOrDefault(data, "itemID", "");
        this.modID = modID;
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

    @Override
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty("itemID", itemID);
        object.addProperty("displayName", displayName);
        object.addProperty("displayNameColor", displayNameColor);
        object.addProperty("xpValue", xpValue);

        return object;
    }
}
