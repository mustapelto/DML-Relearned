package mustapelto.deepmoblearning.client.gui.buttons;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ButtonItemSelect extends GuiButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_select.png");

    private ItemStack stack;
    private boolean selected;

    public ButtonItemSelect(int buttonId, int x, int y, boolean selected, ItemStack stack) {
        super(buttonId, x, y, 18, 18, "");
        this.selected = selected;
        this.stack = stack;
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return super.getHoverState(mouseOver);
    }
}
