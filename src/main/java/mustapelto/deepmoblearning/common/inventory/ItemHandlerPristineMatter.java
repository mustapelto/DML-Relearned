package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ItemHandlerPristineMatter extends ItemStackHandler {
    private MobMetadata pristineType;

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack.getItem() instanceof ItemPristineMatter ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    protected void onContentsChanged(int slot) {
        MobMetadata newPristineType = ItemPristineMatter.getMobMetadata(getStackInSlot(slot));
        if (newPristineType != pristineType) {
            pristineType = newPristineType;
            onPristineTypeChanged();
        }
    }

    protected void onPristineTypeChanged() {}

    public MobMetadata getPristineType() {
        return pristineType;
    }
}
