package mustapelto.deepmoblearning.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public abstract class ContainerBase extends Container {
    private static final int INVENTORY_MARGIN = 9;
    private static final int INVENTORY_SLOT_SIZE = 18;
    private static final int INVENTORY_HOTBAR_OFFSET = 4;

    protected void addInventorySlots(InventoryPlayer inventoryPlayer, int xPosition, int yPosition) {
        // Hotbar
        for (int slot = 0; slot < 9; slot++) {
            int x = xPosition + INVENTORY_MARGIN + INVENTORY_SLOT_SIZE * slot;
            int y = yPosition + INVENTORY_MARGIN + 3 * INVENTORY_SLOT_SIZE + INVENTORY_HOTBAR_OFFSET;
            addSlotToContainer(new Slot(inventoryPlayer, slot, x, y));
        }

        // Main Inventory (starting from bottom)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = xPosition + INVENTORY_MARGIN + INVENTORY_SLOT_SIZE * col;
                int y = yPosition + INVENTORY_MARGIN + INVENTORY_SLOT_SIZE * row;
                int index = col + 9 * row + 9;
                addSlotToContainer(new Slot(inventoryPlayer, index, x, y));
            }
        }
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            itemStack = slot.getStack();
            int stackSize = itemStack.getCount();

            int containerSlots = inventorySlots.size() - playerIn.inventory.mainInventory.size();

            if (index < containerSlots) {
                if (!mergeItemStack(itemStack, containerSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(itemStack, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemStack.getCount() == stackSize) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemStack);
        }

        return itemStack;
    }
}
