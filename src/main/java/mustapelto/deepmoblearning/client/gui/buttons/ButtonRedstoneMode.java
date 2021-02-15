package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.tiles.RedstoneMode;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;

public class ButtonRedstoneMode extends ButtonBase {
    private RedstoneMode redstoneMode;

    public ButtonRedstoneMode(int buttonId, int x, int y, RedstoneMode redstoneMode) {
        super(buttonId, x, y, 18, 18, DMLConstants.Gui.ButtonTextures.REDSTONE_MODE);
        this.redstoneMode = redstoneMode;
    }

    @Override
    protected int getState() {
        return redstoneMode.getIndex();
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
    }

    @Nonnull
    @Override
    public ImmutableList<String> getTooltip() {
        switch (redstoneMode) {
            case ALWAYS_ON:
                return ImmutableList.of(I18n.format("deepmoblearning.redstone_mode.always_on"));
            case HIGH_ON:
                return ImmutableList.of(I18n.format("deepmoblearning.redstone_mode.high_on"));
            case HIGH_OFF:
                return ImmutableList.of(I18n.format("deepmoblearning.redstone_mode.high_off"));
            case ALWAYS_OFF:
                return ImmutableList.of(I18n.format("deepmoblearning.redstone_mode.always_off"));
            default:
                return ImmutableList.of();
        }
    }
}
