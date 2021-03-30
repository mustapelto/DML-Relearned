package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class ButtonStartTrial extends ButtonBase {
    private static ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_start_trial.png");

    public ButtonStartTrial(int buttonId, int x, int y) {
        super(buttonId, x, y, 82, 20, TEXTURE);
    }

    @Override
    public ImmutableList<String> getTooltip() {
        return ImmutableList.of(I18n.format("deepmoblearning.trial_keystone.start_button.tooltip"));
    }
}
