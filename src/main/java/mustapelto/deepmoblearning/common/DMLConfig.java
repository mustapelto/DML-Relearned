package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumDataModelTier;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.enums.EnumMobType;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Comment;

import java.util.HashMap;
import java.util.Map;

@Config(modid = DMLConstants.ModInfo.ID)
@EventBusSubscriber
public class DMLConfig {
    @Name("Living Matter XP")
    @Comment("XP gained by consuming one piece of Living Matter")
    public static final Map<String, Integer> LIVING_MATTER_XP = new HashMap<>();

    static {
        for (EnumLivingMatterType livingMatterType : EnumLivingMatterType.values()) {
            LIVING_MATTER_XP.put(livingMatterType.getName(), livingMatterType.getDefaultXP());
        }
    }

    public static int getLivingMatterXP(EnumLivingMatterType livingMatterType) {
        return LIVING_MATTER_XP.getOrDefault(livingMatterType.getName(), 0);
    }

    @Name("Simulation Tick Costs")
    @Comment("Simulation costs (RF/t) for data models")
    public static final Map<String, Integer> SIMULATION_TICK_COST = new HashMap<>();

    static {
        for (EnumMobType mobType : EnumMobType.values()) {
            if (mobType.isVanilla() || (Loader.isModLoaded(mobType.getModID()))) {
                SIMULATION_TICK_COST.put(mobType.getName(), mobType.getDefaultRFCost());
            }
        }
    }

    public static int getSimulationTickCost(EnumMobType mobType) {
        return SIMULATION_TICK_COST.getOrDefault(mobType.getName(), 0);
    }

    @Name("Data Model Experience Tweaks")
    @Comment("Formula: (Kill multiplier) * (Required kills) = (Total data needed for next tier)" +
            "Please tweak these values responsibly if you're building a modpack for public use.\n" +
            "This mod's intent is not to be grindy or \"time-gated\".\n" +
            "Remember that a high kill multiplier devalues use of the simulation chamber for leveling.\n" +
            "E.g. a kill multiplier of 100 and 2 required kills is the equivalent of 200 simulations.")
    public static final DataModelExperienceTweaks DATA_MODEL_EXPERIENCE_TWEAKS = new DataModelExperienceTweaks();

    public static final class DataModelExperienceTweaks {
        @Comment("Multiplier at Tier")
        public final Map<String, Integer> KILL_MULTIPLIER = new HashMap<>();

        @Comment("Kills required to reach Tier")
        public final Map<String, Integer> DATA_REQUIRED = new HashMap<>();

        DataModelExperienceTweaks() {
            for (EnumDataModelTier tier : EnumDataModelTier.values()) {
                int level = tier.getLevel();
                if (level < DMLConstants.DataModel.MAX_TIER) {
                    KILL_MULTIPLIER.put(tier.getDisplayName(), tier.getKillMultiplierDefault());
                }
                if (level > 0) {
                    DATA_REQUIRED.put(tier.getDisplayName(), tier.getKillsRequiredDefault());
                }
            }
        }

        public int getKillMultiplier(EnumDataModelTier tier) {
            return KILL_MULTIPLIER.getOrDefault(tier.getDisplayName(), 0);
        }

        public int getKillsRequired(EnumDataModelTier tier) {
            return DATA_REQUIRED.getOrDefault(tier.getDisplayName(), 0);
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(DMLConstants.ModInfo.ID)) {
            ConfigManager.sync(DMLConstants.ModInfo.ID, Config.Type.INSTANCE);
        }
    }
}
