package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.tiles.RedstoneMode;
import mustapelto.deepmoblearning.common.tiles.TileEntityMachine;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class ButtonRedstoneMode extends ButtonBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_redstone.png");

    private final TileEntityMachine tileEntity;

    public ButtonRedstoneMode(int buttonId, int x, int y, TileEntityMachine tileEntity) {
        super(buttonId, x, y, 18, 18, TEXTURE);
        this.tileEntity = tileEntity;
        buildTooltip();
    }

    @Override
    public void handleClick(int mouseButton) {
        RedstoneMode mode = tileEntity.getRedstoneMode();
        if (mouseButton == 0)
            tileEntity.setRedstoneMode(mode.next());
        if (mouseButton == 1)
            tileEntity.setRedstoneMode(mode.prev());
        buildTooltip();
    }

    @Override
    protected int getState() {
        return tileEntity.getRedstoneMode().getIndex();
    }

    @Override
    protected void buildTooltip() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        switch (tileEntity.getRedstoneMode()) {
            case ALWAYS_ON:
                builder.add(I18n.format("deepmoblearning.redstone_mode.always_on"));
                break;
            case HIGH_ON:
                builder.add(I18n.format("deepmoblearning.redstone_mode.high_on"));
                break;
            case HIGH_OFF:
                builder.add(I18n.format("deepmoblearning.redstone_mode.high_off"));
                break;
            case ALWAYS_OFF:
                builder.add(I18n.format("deepmoblearning.redstone_mode.always_off"));
                break;
        }
        tooltip = builder.build();
    }
}
