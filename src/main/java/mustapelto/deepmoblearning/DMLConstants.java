package mustapelto.deepmoblearning;

import net.minecraft.util.ResourceLocation;

public class DMLConstants {
    public static final class ModInfo {
        public static final String ID = "deepmoblearning";
        public static final String NAME = "DML Relearned";
        public static final String VERSION = "1.0.0";
        public static final String CONFIG_PATH = "dml_relearned";
    }

    public static final String MINECRAFT = "minecraft";

    public static final class ModDependencies {
        public static final String PATCHOULI = "patchouli";
        public static final String DEP_STRING = "required-after:" + PATCHOULI;
    }

    public static final class Crafting {
        public static final int GLITCH_FRAGMENTS_PER_HEART = 3;
        public static final int SOOTED_REDSTONE_PER_REDSTONE = 1;
    }

    public static final class GlitchSword {
        public static final int DAMAGE_BONUS_INCREASE = 2;
        public static final int DAMAGE_BONUS_MAX = 18;
        public static final int DAMAGE_INCREASE_CHANCE = 6;
    }

    public static final class SimulationChamber {
        public static final int ENERGY_CAPACITY = 2000000;
        public static final int ENERGY_IN_MAX = 25600;
    }

    public static final class LootFabricator {
        public static final int ENERGY_CAPACITY = 2000000;
        public static final int ENERGY_IN_MAX = 25600;
    }

    public static final class TrialKeystone {
        public static final int TRIAL_AREA_RADIUS = 7; // Block radius of area that must be solid blocks, not including keystone itself
        public static final int TRIAL_AREA_HEIGHT = 9; // Block height of area that must be air blocks, not including keystone layer
        public static final int TRIAL_ARENA_RADIUS = 21; // Geometric radius of area inside of which players are considered to be part of a trial
    }

    public static final class DefaultModels {
        public static final ResourceLocation DATA_MODEL = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_default");
        public static final ResourceLocation LIVING_MATTER = new ResourceLocation(DMLConstants.ModInfo.ID, "items/living_matter_default");
        public static final ResourceLocation PRISTINE_MATTER = new ResourceLocation(DMLConstants.ModInfo.ID, "items/pristine_matter_default");
    }

    public static final class Gui {
        public static final int ROW_SPACING = 12;

        public static final class IDs {
            public static final int DEEP_LEARNER = 0;
            public static final int TILE_ENTITY = 1;
        }

        public static final class Colors {
            public static final int AQUA = 0x62D8FF;
            public static final int WHITE = 0xFFFFFF;
            public static final int LIME = 0x00FFC0;
            public static final int BRIGHT_LIME = 0x33EFDC;
            public static final int BRIGHT_PURPLE = 0xC768DB;
        }
    }
}
