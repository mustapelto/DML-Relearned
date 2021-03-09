package mustapelto.deepmoblearning.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotDataModel extends SlotItemHandler {
    public SlotDataModel(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return ItemStackHelper.isDataModel(stack);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
