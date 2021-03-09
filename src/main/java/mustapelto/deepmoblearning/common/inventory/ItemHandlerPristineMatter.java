package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerPristineMatter extends ItemHandlerBase {
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemStackHelper.isPristineMatter(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    public MetadataDataModel getPristineMatterMetadata() {
        return ItemPristineMatter.getDataModelMetadata(getStackInSlot(0));
    }
}
