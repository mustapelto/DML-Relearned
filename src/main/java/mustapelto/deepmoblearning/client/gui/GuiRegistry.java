package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.util.ResourceLocation;

public class GuiRegistry {
    public static final ResourceLocation DEFAULT_GUI = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/default_gui.png");

    public static final class DEEP_LEARNER {
        public static final ResourceLocation BASE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deeplearner_base.png");
        public static final ResourceLocation EXTRAS = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deeplearner_extras.png");
    }
}
