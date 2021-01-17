package mustapelto.deepmoblearning;

import net.minecraftforge.fml.common.Loader;

public class DMLConstants {
    public static final class ModInfo {
        public static final String ID = "deepmoblearning";
        public static final String NAME = "DML Relearned";
        public static final String VERSION = "1.0.0";
    }

    public static final class ModDependencies {
        public static final String PATCHOULI = "patchouli";
        public static final String CRAFT_TWEAKER = "crafttweaker";
        public static final String DEP_STRING = ""; //"";after:" + PATCHOULI + ";after:" + CRAFT_TWEAKER;
        public static boolean isLoaded(String modName) {
            return Loader.isModLoaded(modName);
        }
    }

    public static final class DataModel {
        public static final int MAX_TIER = 4;
    }

    public static final class DeepLearner {
        public static final int INTERNAL_SLOTS = 4;
    }

    public static final class LivingMatter {
        public static final class DEFAULT_VALUES {
            public static final class OVERWORLDIAN {
                public static final String ID = "overworldian";
                public static final int XP = 10;
            }
            public static final class HELLISH {
                public static final String ID = "hellish";
                public static final int XP = 14;
            }
            public static final class EXTRATERRESTRIAL {
                public static final String ID = "extraterrestrial";
                public static final int XP = 20;
            }
            public static final class TWILIGHT {
                public static final String ID = "twilight";
                public static final int XP = 30;
            }
        }
    }

    public static final String MINECRAFT = "minecraft";

    public static final class GuiIDs {
        public static final int DEEP_LEARNER = 0;
    }

    public static final class GuiColors {
        public static final int AQUA = 0x62D8FF;
        public static final int WHITE = 0xFFFFFF;
        public static final int LIME = 0x00FFC0;
        public static final int BRIGHT_LIME = 0x33EFDC;
        public static final int BRIGHT_PURPLE = 0xC768DB;
    }
}
