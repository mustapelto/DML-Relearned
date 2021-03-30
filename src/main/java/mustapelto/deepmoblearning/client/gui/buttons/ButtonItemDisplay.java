package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;

public class ButtonItemDisplay extends ButtonBase {
    private final ItemStack stack;

    public ButtonItemDisplay(int buttonId, int x, int y, ItemStack stack) {
        super(buttonId, x, y, 18, 18, null);
        this.stack = stack;
    }

    @Override
    public ImmutableList<String> getTooltip() {
        return ImmutableList.of(stack.getDisplayName());
    }

    public ItemStack getStack() {
        return stack;
    }
}
