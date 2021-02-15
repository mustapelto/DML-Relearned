package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ButtonItemSelect extends ButtonBase {
    private final ItemStack stack;
    private final int index;
    private boolean selected;

    public ButtonItemSelect(int buttonId, int x, int y, ItemStack stack, int index, boolean selected) {
        super(buttonId, x, y, 18, 18, DMLConstants.Gui.ButtonTextures.ITEM_SELECT);
        this.stack = stack;
        this.index = index;
        this.selected = selected;
    }

    @Nonnull
    @Override
    public ImmutableList<String> getTooltip() {
        return ImmutableList.of(stack.getDisplayName());
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIndex() {
        return index;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    protected int getState() {
        return selected ? 1 : 0;
    }
}
