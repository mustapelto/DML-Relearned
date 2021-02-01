package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemDataModel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerInputDataModel extends ItemHandlerBase {
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack.getItem() instanceof ItemDataModel ? super.insertItem(slot, stack, simulate) : stack;
    }
}
