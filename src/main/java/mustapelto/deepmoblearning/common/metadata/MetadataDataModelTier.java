package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.common.util.JsonHelper;
import mustapelto.deepmoblearning.common.util.StringHelper;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by mustapelto on 2021-02-14
 */
public class MetadataDataModelTier extends Metadata {
    public static final MetadataDataModelTier INVALID = new MetadataDataModelTier();

    // Data from JSON
    private final String displayName; // Name shown in tooltips and GUI (Default = "")
    private final TextFormatting displayColor; // Color of name when displayed (Default = "white")
    private final int killMultiplier; // Amount of data gained from one kill at this tier (Range = 0 - 1000, Default = 1)
    private final int dataToNext; // Amount of data (through kills or simulations) required to reach next tier (Range = 0 - 10000, Default = 10)
    private final int pristineChance; // Chance (%) to get Pristine Matter on each iteration (Range = 0 - 100, Default = 10)
    private final boolean canSimulate; // Can a Data Model of this tier be used in a simulation chamber? (Default = true)

    private MetadataDataModelTier() {
        super("", "");
        displayName = "INVALID";
        displayColor = TextFormatting.WHITE;
        killMultiplier = 0;
        dataToNext = Integer.MAX_VALUE;
        pristineChance = 0;
        canSimulate = false;
    }

    public MetadataDataModelTier(JsonObject data, String level) {
        super("", "");

        displayName = JsonHelper.getString(data, "displayName", "Tier " + level);
        String displayColorString = JsonHelper.getString(data, "displayColor", "white");
        TextFormatting displayFormatting = TextFormatting.getValueByName(displayColorString);
        displayColor = (displayFormatting != null) ? displayFormatting : TextFormatting.WHITE;
        killMultiplier = JsonHelper.getInt(data, "killMultiplier", 1, 0, 1000);
        dataToNext = JsonHelper.getInt(data, "dataToNext", 10, 0, 10000);
        pristineChance = JsonHelper.getInt(data, "pristineChance", 10, 0, 100);
        canSimulate = JsonHelper.getBoolean(data, "canSimulate", true);
    }

    @Override
    public void finalizeData() {} // Nothing to do here

    @Override
    public boolean isModLoaded() {
        return true; // Irrelevant for Data Model Tiers
    }

    @Override
    public boolean isInvalid() {
        return this.equals(INVALID);
    }

    public String getDisplayNameFormatted(String template) {
        String completeString = String.format(template, displayName);
        return StringHelper.getFormattedString(completeString, displayColor);
    }

    public String getDisplayNameFormatted() {
        return getDisplayNameFormatted("%s");
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

    public int getPristineChance() {
        return pristineChance;
    }
}
