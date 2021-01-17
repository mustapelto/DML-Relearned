package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class DataModelTierData extends MetaDataBase {
    private final int level; // Tier level. Generated from JSON element order.
    private final String displayName; // Name shown in tooltips and GUI.
    private final String displayColor; // Color of name when displayed.
    private final int killMultiplier; // Amount of data gained from one kill at this tier.
    private final int dataToNext; // Amount of data (through kills or simulations) required to reach next tier.
    private final int pristineChance; // Chance (%) to get Pristine Matter on each iteration

    public DataModelTierData(int level, JsonObject data) {
        validate(data, new String[]{"displayName"}, "Data Model Tier Data");

        this.level = level;
        displayName = getOrDefault(data, "displayName", "");
        displayColor = getOrDefault(data, "displayColor", "white");
        killMultiplier = getOrDefault(data, "killMultiplier", 0, 0, Integer.MAX_VALUE);
        dataToNext = getOrDefault(data, "dataToNext", 0, 0, Integer.MAX_VALUE);
        pristineChance = getOrDefault(data, "pristineChance", 0, 0, 100);
    }

    public String getDisplayNameFormatted() {
        TextFormatting formatting = TextFormatting.getValueByName(displayColor);

        return (formatting != null) ?
                formatting + displayName + TextFormatting.RESET :
                displayName;
    }

    public int getLevel() {
        return level;
    }

    public int getKillMultiplier() {
        return killMultiplier;
    }

    public int getDataToNext() {
        return dataToNext;
    }

    @Override
    public boolean isModLoaded() {
        return true; // Tiers are mod-independent. This should never be called but added as precaution.
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty("displayName", displayName);
        object.addProperty("displayColor", displayColor);
        if (killMultiplier > 0)
            object.addProperty("killMultiplier", killMultiplier);
        if (dataToNext > 0)
            object.addProperty("dataToNext", dataToNext);
        if (pristineChance > 0)
            object.addProperty("pristineChance", pristineChance);

        return object;
    }
}
