package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerInputPristine extends ItemHandlerBase {
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack.getItem() instanceof ItemPristineMatter ? super.insertItem(slot, stack, simulate) : stack;
    }
}
