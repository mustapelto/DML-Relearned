package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class LivingMatterData extends MetaDataBase {
    private final String displayName; // Name shown in tooltips and GUI. Also used for item display name.
    private final String displayColor; // Color of name when displayed.
    private final int xpValue; // XP received when item is consumed.

    public LivingMatterData(String modID, JsonObject data) {
        validate(data, new String[]{"itemID"}, "Living Matter Data"); // itemID required field for item generation

        itemID = getOrDefault(data, "itemID", "");
        this.modID = modID;
        displayName = getOrDefault(data, "displayName", "");
        displayColor = getOrDefault(data, "displayColor", "white");
        xpValue = getOrDefault(data, "xpValue", 0, 0, Integer.MAX_VALUE);
    }

    public String getDisplayNameFormatted() {
        TextFormatting formatting = TextFormatting.getValueByName(displayColor);
        return (formatting != null) ?
                formatting + displayName + TextFormatting.RESET :
                displayName;
    }

    public int getXpValue() {
        return xpValue;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty("itemID", itemID);
        object.addProperty("displayName", displayName);
        object.addProperty("displayColor", displayColor);
        object.addProperty("xpValue", xpValue);

        return object;
    }
}
