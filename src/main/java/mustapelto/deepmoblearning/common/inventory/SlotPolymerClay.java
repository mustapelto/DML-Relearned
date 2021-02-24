package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotPolymerClay extends SlotItemHandler {
    public SlotPolymerClay(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return ItemStackHelper.isPolymerClay(stack);
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }
}
