package mustapelto.deepmoblearning;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class DMLConstants {
    public static final class ModInfo {
        public static final String ID = "deepmoblearning";
        public static final String NAME = "DML Relearned";
        public static final String VERSION = "1.0.0";
    }

    public static final String MINECRAFT = "minecraft";

    public static final class ModDependencies {
        public static final String PATCHOULI = "patchouli";
        public static final String CRAFT_TWEAKER = "crafttweaker";
        public static final String DEP_STRING = ""; //"";after:" + PATCHOULI + ";after:" + CRAFT_TWEAKER;
        public static boolean isLoaded(String modName) {
            return Loader.isModLoaded(modName);
        }
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

    public static final class Gui {
        public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/default_gui.png");
        public static final int ROW_SPACING = 12;

        public static final class IDs {
            public static final int DEEP_LEARNER = 0;
            public static final int MACHINE = 1;
            public static final int TRIAL_KEYSTONE = 2;
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
