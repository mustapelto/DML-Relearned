package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class ItemHandlerTrialKey extends ItemHandlerBase {
    public ItemHandlerTrialKey() {
        super();
    }

    public ItemHandlerTrialKey(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return ItemStackHelper.isTrialKey(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }
}
