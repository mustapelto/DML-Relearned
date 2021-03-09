package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemHandlerTrialKey extends ItemHandlerBase {
    public ItemHandlerTrialKey() {
        super();
    }

    public ItemHandlerTrialKey(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemStackHelper.isTrialKey(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }
}
