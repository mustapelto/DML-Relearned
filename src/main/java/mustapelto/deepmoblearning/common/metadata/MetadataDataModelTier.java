package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.StringHelper;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by mustapelto on 2021-02-14
 */
public class MetadataDataModelTier extends Metadata {
    // JSON Keys
    private static final String TIER = "tier";
    private static final String DISPLAY_NAME = "displayName";
    private static final String DISPLAY_COLOR = "displayColor";
    private static final String KILL_MULTIPLIER = "killMultiplier";
    private static final String DATA_TO_NEXT = "dataToNext";
    private static final String PRISTINE_CHANCE = "pristineChance";
    private static final String CAN_SIMULATE = "canSimulate";
    private static final String TRIAL = "trial";

    // Validation
    private static final String[] REQUIRED_KEYS = new String[] {
            TIER
    };

    // Default Values
    private static final int DEFAULT_TIER = -1;
    private static final String DEFAULT_DISPLAY_NAME = "Tier %s";
    private static final String DEFAULT_DISPLAY_COLOR = "white";
    private static final int DEFAULT_KILL_MULTIPLIER = 1;
    private static final int DEFAULT_DATA_TO_NEXT = 10;
    private static final int DEFAULT_PRISTINE_CHANCE = 10;
    private static final boolean DEFAULT_CAN_SIMULATE = true;

    // Data from JSON
    private final int tier; // Integer value of tier (stored in ItemStack NBT)
    private final String displayName; // Name shown in tooltips and GUI (Default = "")
    private final int killMultiplier; // Amount of data gained from one kill at this tier (Range = 0 - 1000, Default = 1)
    private final int dataToNext; // Amount of data (through kills or simulations) required to reach next tier (Range = 0 - 10000, Default = 10)
    private final int pristineChance; // Chance (%) to get Pristine Matter on each iteration (Range = 0 - 100, Default = 10)
    private final boolean canSimulate; // Can a Data Model of this tier be used in a simulation chamber? (Default = true)
    private final TierTrialData tierTrialData; // Data about Trials at this tier

    // Calculated data
    private final TextFormatting displayColor; // Display color
    private final String displayNameFormatted; // Display name formatted with display color

    public MetadataDataModelTier(JsonObject data) throws IllegalArgumentException {
        if (isInvalidJson(data, REQUIRED_KEYS)) {
            throw new IllegalArgumentException("Invalid Data Model Tier JSON entry!");
        }
        tier = getInt(data, TIER, 0, 100)
                .orElse(DEFAULT_TIER);
        displayName = getString(data, DISPLAY_NAME)
                .orElse(String.format(DEFAULT_DISPLAY_NAME, tier));

        String displayColorString = getString(data, DISPLAY_COLOR)
                .orElse(DEFAULT_DISPLAY_COLOR);
        displayColor = StringHelper.getValidFormatting(displayColorString);
        displayNameFormatted = StringHelper.getFormattedString(displayName, displayColor);

        killMultiplier = getInt(data, KILL_MULTIPLIER, 1, 1000)
                .orElse(DEFAULT_KILL_MULTIPLIER);
        dataToNext = getInt(data, DATA_TO_NEXT, 1, 10000)
                .orElse(DEFAULT_DATA_TO_NEXT);
        pristineChance = getInt(data, PRISTINE_CHANCE, 0, 100)
                .orElse(DEFAULT_PRISTINE_CHANCE);
        canSimulate = getBoolean(data, CAN_SIMULATE)
                .orElse(DEFAULT_CAN_SIMULATE);

        JsonObject trialDataJSON = getJsonObject(data, TRIAL)
                .orElse(null);
        if (trialDataJSON == null) {
            tierTrialData = TierTrialData.DEFAULT;
            DMLRelearned.logger.warn("Invalid Trial entry in Data Model Tier JSON. Using default values.");
        } else {
            tierTrialData = new TierTrialData(trialDataJSON);
        }
    }

    @Override
    public void finalizeData() {} // Nothing to do here

    @Override
    public String getID() {
        return String.valueOf(tier);
    }

    public int getTier() {
        return tier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameFormatted() {
        return displayNameFormatted;
    }

    public TextFormatting getDisplayColor() {
        return displayColor;
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

    public TierTrialData getTierTrialData() {
        return tierTrialData;
    }

    public static class TierTrialData {
        public static final TierTrialData DEFAULT = new TierTrialData();

        private static final int DEFAULT_PRISTINE = 2;
        private static final int DEFAULT_MAX_WAVE = 10;
        private static final int DEFAULT_AFFIXES = 0;
        private static final int DEFAULT_GLITCH_CHANCE = 5;

        private final int pristine; // Amount of Pristine Matter gained when completing trial of this tier (Range = 0 - 64, Default = 2)
        private final int maxWave; // Highest possible wave of trial (Range = -1 - INT_MAX, Default = -1; -1 = as many as there are)
        private final int affixes; // Amount of affixes this trial will get (Range = 0 - 3, Default = 0)
        private final int glitchChance; // Chance to produce a Glitch (Range = 0 - 100, Default = 5)

        private TierTrialData() {
            pristine = DEFAULT_PRISTINE;
            maxWave = DEFAULT_MAX_WAVE;
            affixes = DEFAULT_AFFIXES;
            glitchChance = DEFAULT_GLITCH_CHANCE;
        }

        public TierTrialData(JsonObject data) {
            pristine = getInt(data, "pristine", 0, 64)
                    .orElse(DEFAULT_PRISTINE);
            maxWave = getInt(data, "maxWave", 0, 100)
                    .orElse(DEFAULT_MAX_WAVE);
            affixes = getInt(data, "affixes", 0, 3)
                    .orElse(DEFAULT_AFFIXES);
            glitchChance = getInt(data, "glitchChance", 0, 100)
                    .orElse(DEFAULT_GLITCH_CHANCE);
        }

        public int getPristine() {
            return pristine;
        }

        public int getAffixes() {
            return affixes;
        }

        public int getGlitchChance() {
            return glitchChance;
        }

        public int getMaxWave() {
            return maxWave;
        }
    }
}
