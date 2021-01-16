package mustapelto.deepmoblearning;

import mustapelto.deepmoblearning.common.enums.EnumDataModelTier;
import mustapelto.deepmoblearning.common.util.MathHelper;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Comment;

import java.util.HashMap;
import java.util.Map;

@Config(modid = DMLConstants.ModInfo.ID, name = "dml_relearned/dmlRelearned", category = "")
@EventBusSubscriber
public class DMLConfig {
    @Name("Data Model Experience Tweaks")
    @Comment("Formula: (Kill multiplier) * (Required kills) = (Total data needed for next tier)" +
            "Please tweak these values responsibly if you're building a modpack for public use.\n" +
            "This mod's intent is not to be grindy or \"time-gated\".\n" +
            "Remember that a high kill multiplier devalues use of the simulation chamber for leveling.\n" +
            "E.g. a kill multiplier of 100 and 2 required kills is the equivalent of 200 simulations.")
    public static final DataModelExperienceTweaks DATA_MODEL_EXPERIENCE_TWEAKS = new DataModelExperienceTweaks();

    public static final class DataModelExperienceTweaks {
        @Comment("Multiplier at Tier (min = 1)")
        public final Map<String, Integer> KILL_MULTIPLIER = new HashMap<>();

        @Comment("Kills required to reach Tier (min = 0)")
        public final Map<String, Integer> DATA_REQUIRED = new HashMap<>();

        DataModelExperienceTweaks() {
            for (EnumDataModelTier tier : EnumDataModelTier.values()) {
                int level = tier.getLevel();
                if (level < DMLConstants.DataModel.MAX_TIER) {
                    KILL_MULTIPLIER.put(tier.getLevelString(), tier.getKillMultiplierDefault());
                }
                if (level > 0) {
                    DATA_REQUIRED.put(tier.getLevelString(), tier.getKillsRequiredDefault());
                }
            }
        }

        public int getKillMultiplier(EnumDataModelTier tier) {
            return MathHelper.Clamp(KILL_MULTIPLIER.getOrDefault(tier.getLevelString(), 1), 1, Integer.MAX_VALUE);
        }

        public int getDataRequired(EnumDataModelTier tier) {
            return MathHelper.Clamp(DATA_REQUIRED.getOrDefault(tier.getLevelString(), 0), 1, Integer.MAX_VALUE);
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(DMLConstants.ModInfo.ID)) {
            ConfigManager.sync(DMLConstants.ModInfo.ID, Config.Type.INSTANCE);
        }
    }
}
