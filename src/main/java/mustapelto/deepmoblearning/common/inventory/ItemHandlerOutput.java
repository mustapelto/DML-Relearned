package mustapelto.deepmoblearning.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class ItemHandlerOutput extends ItemHandlerBase {
    public ItemHandlerOutput() {
        super();
    }

    public ItemHandlerOutput(int size) {
        super(size);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack;
    }

    /** Iterate through available slots and fill with input item
     *  until either input stack is empty or all slots are filled
     *
     * @param stack input ItemStack
     */
    public void addItemToAvailableSlots(ItemStack stack) {
        int i = 0;
        while (i < getSlots() && !stack.isEmpty()) {
            ItemStack currentSlotStack = getStackInSlot(i);

            if (currentSlotStack.isEmpty()) {
                // current slot is empty -> insert entire (remaining) stack
                setStackInSlot(i, stack.copy());
                return;
            }

            int currentSlotStackSize = currentSlotStack.getCount();
            int currentSlotStackMaxSize = currentSlotStack.getMaxStackSize();
            int currentSlotRemainingSpace = currentSlotStackMaxSize - currentSlotStackSize;

            if (currentSlotRemainingSpace > 0 &&
                    ItemHandlerHelper.canItemStacksStack(stack, currentSlotStack)) {
                // Current slot has room left and is same item as input stack
                int remaining = growItem(i, stack.getCount());
                stack.setCount(remaining);
            }

            i++;
        }
    }

    public boolean hasRoomForItem(ItemStack stack) {
        if (stack.isEmpty())
            return true;

        int availableSpaceForItem = 0;

        for (int i = 0; i < getSlots(); i++) {
            ItemStack currentSlotStack = getStackInSlot(i);
            if (currentSlotStack.isEmpty())
                availableSpaceForItem += stack.getMaxStackSize();
            else if (ItemHandlerHelper.canItemStacksStack(stack, currentSlotStack))
                availableSpaceForItem += stack.getMaxStackSize() - currentSlotStack.getCount();
        }

        return availableSpaceForItem >= stack.getCount();
    }
}
