package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public abstract class ItemHandlerPristineMatter extends ItemHandlerBase {
    @Nullable
    protected MetadataDataModel pristineMatterMetadata;

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemStackHelper.isPristineMatter(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        updateMetadata();
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (slot != 0)
            return;

        updateMetadata();
    }

    @Nullable
    public MetadataDataModel getPristineMatterMetadata() {
        return pristineMatterMetadata;
    }

    private void updateMetadata() {
        MetadataDataModel newMetadata = ItemPristineMatter.getDataModelMetadata(getStackInSlot(0)).orElse(null);
        if (newMetadata != pristineMatterMetadata) {
            pristineMatterMetadata = newMetadata;
            onMetadataChanged();
        }
    }

    protected abstract void onMetadataChanged();
}
