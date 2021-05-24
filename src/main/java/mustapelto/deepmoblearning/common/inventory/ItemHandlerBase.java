package mustapelto.deepmoblearning.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public abstract class ItemHandlerBase extends ItemStackHandler {
    public ItemHandlerBase() {
        super();
    }

    public ItemHandlerBase(int size) {
        super(size);
    }

    public ItemHandlerBase(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public int voidItem(int slot, int amount) {
        ItemStack newStack = getStackInSlot(slot).copy();
        int oldCount = newStack.getCount();
        int newCount = Math.max(0, oldCount - amount);
        int toVoid = oldCount - newCount;
        newStack.shrink(toVoid);
        if (newStack.isEmpty())
            newStack = ItemStack.EMPTY;
        setStackInSlot(slot, newStack);

        return amount - toVoid;
    }

    public int voidItem() {
        return voidItem(0, 1);
    }

    public int growItem(int slot, int amount) {
        ItemStack newStack = getStackInSlot(slot).copy();
        int oldCount = newStack.getCount();
        int newCount = Math.min(newStack.getMaxStackSize(), oldCount + amount);
        int toAdd = newCount - oldCount;
        newStack.grow(toAdd);
        setStackInSlot(slot, newStack);

        return amount - toAdd;
    }

    public int growItem(int amount) {
        return growItem(0, amount);
    }

    public int growItem() {
        return growItem(0, 1);
    }
}
