package mustapelto.deepmoblearning.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotPolymerClay extends SlotItemHandler {
    public SlotPolymerClay(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return ItemStackHelper.isPolymerClay(stack);
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }
}
