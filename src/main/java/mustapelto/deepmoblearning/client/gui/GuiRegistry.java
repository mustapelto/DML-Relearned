package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.util.ResourceLocation;

public class GuiRegistry {
    public static final ResourceLocation DEFAULT_GUI = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/default_gui.png");

    public static final class DEEP_LEARNER {
        public static final ResourceLocation BASE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deep_learner_base.png");
        public static final ResourceLocation EXTRAS = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deep_learner_extras.png");
    }

    public static final class DATA_OVERLAY {
        public static final ResourceLocation EXP_BAR = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/experience_gui.png");
    }

    public static final class SIMULATION_CHAMBER {
        public static final ResourceLocation BASE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/simulation_chamber_base.png");
    }

    public static final class BUTTONS {
        public static final ResourceLocation REDSTONE_BUTTON = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_redstone.png");
    }
}
