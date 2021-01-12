package mustapelto.deepmoblearning.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ContainerBase extends Container {
    protected final InventoryPlayer inventoryPlayer;
    protected final World world;

    protected ContainerBase(InventoryPlayer inventoryPlayer, World world) {
        this.inventoryPlayer = inventoryPlayer;
        this.world = world;
    }

    protected void addInventorySlots(InventoryPlayer inventoryPlayer, int xPosition, int yPosition) {
        // Hotbar
        for (int slot = 0; slot < 9; slot++) {
            addSlotToContainer(new Slot(inventoryPlayer, slot, xPosition + 18 * slot, yPosition + 58));
        }

        // Main Inventory (starting from bottom)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = xPosition + 18 * col;
                int y = yPosition + 18 * row;
                int index = col + 9 * row + 9;
                addSlotToContainer(new Slot(inventoryPlayer, index, x, y));
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
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
