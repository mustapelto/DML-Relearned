package mustapelto.deepmoblearning.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public abstract class ItemHandlerNBTBase extends ItemHandlerBase {
    public ItemHandlerNBTBase() {
        super();
    }

    public ItemHandlerNBTBase(int size) {
        super(size);
    }

    public ItemHandlerNBTBase(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public void deserializeAndConvertNBT(NBTTagCompound nbt) {
        deserializeNBT(nbt);

        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = stacks.get(i);
            if (isLegacyItemStack(stack))
                stacks.set(i, convertLegacyItemStack(stack));
        }
    }

    protected abstract boolean isLegacyItemStack(ItemStack stack);
    protected abstract ItemStack convertLegacyItemStack(ItemStack stack);
}
