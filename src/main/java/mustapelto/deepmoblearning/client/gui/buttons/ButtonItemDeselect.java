package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ButtonItemDeselect extends ButtonBase {
    private ItemStack displayStack = ItemStack.EMPTY;

    public ButtonItemDeselect(int buttonId, int x, int y) {
        super(buttonId, x, y, 18, 18, null);
    }

    public void setDisplayStack(ItemStack item) {
        displayStack = item;
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    @Nonnull
    @Override
    public ImmutableList<String> getTooltip() {
        return ImmutableList.of(displayStack == ItemStack.EMPTY ? "" : I18n.format("deepmoblearning.loot_fabricator.tooltip.deselect"));
    }
}
