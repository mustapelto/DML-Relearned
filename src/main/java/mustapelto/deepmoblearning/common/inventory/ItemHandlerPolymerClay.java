package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import net.minecraft.item.ItemStack;

public class ItemHandlerPolymerClay extends ItemHandlerBase {
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemStackHelper.isPolymerClay(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }
}
