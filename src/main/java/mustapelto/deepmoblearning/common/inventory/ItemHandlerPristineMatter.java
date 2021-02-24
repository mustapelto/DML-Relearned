package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerPristineMatter extends ItemHandlerBase {
    private MetadataDataModel metadata;

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return ItemStackHelper.isPristineMatter(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    protected void onLoad() {
        metadata = ItemPristineMatter.getDataModelMetadata(getStackInSlot(0));
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (slot != 0)
            return;

        MetadataDataModel newMetadata = ItemPristineMatter.getDataModelMetadata(getStackInSlot(0));
        if (newMetadata != metadata) {
            metadata = newMetadata;
            onPristineTypeChanged();
        }
    }

    protected void onPristineTypeChanged() {}

    public MetadataDataModel getDataModelMetadata() {
        return metadata;
    }
}
