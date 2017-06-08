package mustapelto.deepmoblearning;

import net.minecraftforge.fml.common.Loader;

public class DMLConstants {
    public static final class ModInfo {
        public static final String ID = "deepmoblearning";
        public static final String NAME = "DML Relearned";
        public static final String VERSION = "1.0.0";
    }

    public static final class ModDependencies {
        public static final String TWILIGHT = "twilightforest";
        public static final String THERMAL = "thermalfoundation";
        public static final String TINKERS = "tconstruct";
        public static final String MO = "matteroverdrive";
        public static final String PATCHOULI = "patchouli";
        public static final String CRAFT_TWEAKER = "crafttweaker";
        public static final String DEP_STRING = "after:" + TWILIGHT + ";after:" + THERMAL + ";after:" + TINKERS +
                ";after:" + MO + ";after:" + PATCHOULI + ";after:" + CRAFT_TWEAKER;
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

    public static final String VANILLA = "vanilla";

    public static final class GuiIDs {
        public static final int DEEP_LEARNER = 0;
    }
}
