package mustapelto.deepmoblearning.common.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerOutput extends ItemHandlerBase {
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }
}
