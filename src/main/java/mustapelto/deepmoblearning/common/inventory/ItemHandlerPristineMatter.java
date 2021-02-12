package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ItemHandlerPristineMatter extends ItemStackHandler {
    private MobMetadata mobMetadata;

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack.getItem() instanceof ItemPristineMatter ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    protected void onLoad() {
        mobMetadata = ItemPristineMatter.getMobMetadata(getStackInSlot(0));
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (slot != 0)
            return;

        MobMetadata newMobMetadata = ItemPristineMatter.getMobMetadata(getStackInSlot(0));
        if (newMobMetadata != mobMetadata) {
            mobMetadata = newMobMetadata;
            onPristineTypeChanged();
        }
    }

    protected void onPristineTypeChanged() {}

    public MobMetadata getMobMetadata() {
        return mobMetadata;
    }
}
