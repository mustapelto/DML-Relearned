package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import net.minecraft.util.text.TextFormatting;

import static mustapelto.deepmoblearning.common.util.JsonHelper.getOrDefault;

public class DataModelTierData {
    private final String displayName; // Name shown in tooltips and GUI. (Default = "")
    private final String displayColor; // Color of name when displayed. (Default = "white")
    private final int killMultiplier; // Amount of data gained from one kill at this tier. (Range = 0 - MAX_INT, Default = 1)
    private final int dataToNext; // Amount of data (through kills or simulations) required to reach next tier. (Range = 0 - MAX_INT, Default = 10)
    private final int pristineChance; // Chance (%) to get Pristine Matter on each iteration (Range = 0 - 100, Default = 10)
    private final boolean canSimulate; // Can a Data Model of this tier be used in a simulation chamber? (Default = true)

    public DataModelTierData(int level, JsonObject data) {
        displayName = getOrDefault(data, "displayName", "Tier " + level);
        displayColor = getOrDefault(data, "displayColor", "white");
        killMultiplier = getOrDefault(data, "killMultiplier", 1, 0, Integer.MAX_VALUE);
        dataToNext = getOrDefault(data, "dataToNext", 10, 0, Integer.MAX_VALUE);
        pristineChance = getOrDefault(data, "pristineChance", 10, 0, 100);
        canSimulate = getOrDefault(data, "canSimulate", true);
    }

    public String getDisplayNameFormatted() {
        return getDisplayNameFormatted("%s");
    }

    public String getDisplayNameFormatted(String template) {
        TextFormatting formatting = TextFormatting.getValueByName(displayColor);

        String text = String.format(template, displayName);

        return (formatting != null) ?
                formatting + text + TextFormatting.RESET :
                text;
    }

    public int getKillMultiplier() {
        return killMultiplier;
    }

    public int getDataToNext() {
        return dataToNext;
    }

    public boolean getCanSimulate() {
        return canSimulate;
    }

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
        if (!canSimulate)
            object.addProperty("canSimulate", false);

        return object;
    }
}
