package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.items.ItemLegacyDataModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class ItemHandlerDataModel extends ItemHandlerNBTBase {
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
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return ItemDataModel.isDataModel(stack);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemDataModel.isDataModel(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    protected boolean isLegacyItemStack(ItemStack stack) {
        return stack.getItem() instanceof ItemLegacyDataModel;
    }

    @Override
    protected ItemStack convertLegacyItemStack(ItemStack stack) {

    }
}
