package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemHandlerDataModel extends ItemHandlerBase {
    public ItemHandlerDataModel() {
        super();
    }

    public ItemHandlerDataModel(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public NonNullList<ItemStack> getItemStacks() {
        return stacks;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemStackHelper.isDataModel(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }
}
