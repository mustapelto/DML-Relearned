package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemPolymerClay;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerPolymerClay extends ItemHandlerBase {
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack.getItem() instanceof ItemPolymerClay ? super.insertItem(slot, stack, simulate) : stack;
    }
}
