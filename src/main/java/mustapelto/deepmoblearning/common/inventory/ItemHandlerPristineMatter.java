package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class ItemHandlerPristineMatter extends ItemHandlerBase {
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemStackHelper.isPristineMatter(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    public Optional<MetadataDataModel> getPristineMatterMetadata() {
        return ItemPristineMatter.getDataModelMetadata(getStackInSlot(0));
    }
}
