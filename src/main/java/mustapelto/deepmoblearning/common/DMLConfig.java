package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumDataModelTier;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.enums.EnumMobType;
import mustapelto.deepmoblearning.common.util.MathHelper;
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

@Config(modid = DMLConstants.ModInfo.ID, name = "dml-relearned", category = "")
@EventBusSubscriber
public class DMLConfig {
    @Name("Living Matter Settings")
    public static final LivingMatter LIVING_MATTER = new LivingMatter();

    public static final class LivingMatter {
        @Name("Living Matter XP")
        @Comment("XP gained by consuming one piece of Living Matter (min = 0)")
        public final Map<String, Integer> CONSUME_XP_GAIN = new HashMap<>();

        LivingMatter() {
            for (EnumLivingMatterType livingMatterType : EnumLivingMatterType.values()) {
                CONSUME_XP_GAIN.put(livingMatterType.getName(), livingMatterType.getDefaultXP());
            }
        }

        public int getLivingMatterXP(EnumLivingMatterType livingMatterType) {
            return MathHelper.Clamp(CONSUME_XP_GAIN.getOrDefault(livingMatterType.getName(), 0), 0, Integer.MAX_VALUE);
        }
    }

    @Name("Simulation Chamber Settings")
    public static final SimulationChamber SIMULATION_CHAMBER = new SimulationChamber();

    public static final class SimulationChamber {
        @Name("Simulation Tick Costs")
        @Comment("Simulation costs (RF/t) for data models (min = 0)")
        public final Map<String, Integer> SIMULATION_TICK_COST = new HashMap<>();

        SimulationChamber() {
            for (EnumMobType mobType : EnumMobType.values()) {
                if (mobType.isVanilla() || (Loader.isModLoaded(mobType.getModID()))) {
                    SIMULATION_TICK_COST.put(mobType.getName(), mobType.getDefaultRFCost());
                }
            }
        }

        public int getSimulationTickCost(EnumMobType mobType) {
            return MathHelper.Clamp(SIMULATION_TICK_COST.getOrDefault(mobType.getName(), 0), 0, Integer.MAX_VALUE);
        }
    }

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
                    KILL_MULTIPLIER.put(tier.getName(), tier.getKillMultiplierDefault());
                }
                if (level > 0) {
                    DATA_REQUIRED.put(tier.getName(), tier.getKillsRequiredDefault());
                }
            }
        }

        public int getKillMultiplier(EnumDataModelTier tier) {
            return MathHelper.Clamp(KILL_MULTIPLIER.getOrDefault(tier.getName(), 1), 1, Integer.MAX_VALUE);
        }

        public int getDataRequired(EnumDataModelTier tier) {
            return MathHelper.Clamp(DATA_REQUIRED.getOrDefault(tier.getName(), 0), 1, Integer.MAX_VALUE);
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(DMLConstants.ModInfo.ID)) {
            ConfigManager.sync(DMLConstants.ModInfo.ID, Config.Type.INSTANCE);
        }
    }
}
