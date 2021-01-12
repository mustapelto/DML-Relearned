package mustapelto.deepmoblearning.common.handlers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerBase extends ItemStackHandler {
    public ItemHandlerBase() {
        super();
    }

    public ItemHandlerBase(int size) {
        super(size);
    }

    public ItemHandlerBase(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public NonNullList<ItemStack> getItemStacks() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for (int i = 0; i < getSlots(); i++) {
            stacks.add(getStackInSlot(i));
        }
        return stacks;
    }

    public boolean canInsertItem(ItemStack stack) {
        int availableSpace = 0;

        for (int i = 0; i < getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack.isEmpty()) {
                availableSpace += stack.getMaxStackSize();
            } else if(ItemStack.areItemsEqual(slotStack, stack)) {
                availableSpace += stack.getMaxStackSize() - slotStack.getCount();
            }
        }

        return availableSpace >= stack.getCount();
    }

    public ItemStack addToFirstAvailableSlot(ItemStack stack) {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);
            int slotStackSize = slotStack.getCount();
            int insertStackSize = stack.getCount();

            if (slotStack.isEmpty()) { // found empty slot -> insert entire stack
                setStackInSlot(i, stack.copy());
                return ItemStack.EMPTY;
            }

            // slot with same item and space left -> insert as much as possible and return rest
            if (slotStackSize < slotStack.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(slotStack, stack)) {
                int availableSpace = slotStack.getMaxStackSize() - slotStackSize;

                if (insertStackSize <= availableSpace) {
                    slotStack.grow(insertStackSize);
                    return ItemStack.EMPTY;
                } else {
                    slotStack.grow(availableSpace);
                    int newSize = insertStackSize - availableSpace;
                    return ItemHandlerHelper.copyStackWithSize(stack, newSize);
                }
            }
        }

        return stack;
    }
}
