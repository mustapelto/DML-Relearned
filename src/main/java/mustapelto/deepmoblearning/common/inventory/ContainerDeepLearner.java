package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.stackhandlers.ItemHandlerBase;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerDeepLearner extends ContainerBase {
    private final ItemHandlerBase itemHandler;

    private final int deepLearnerSlot; // inventory slot to make inaccessible while GUI open
    private final ItemStack deepLearner;
    private final EntityEquipmentSlot equipmentSlot;

    public ContainerDeepLearner(InventoryPlayer inventoryPlayer, World world, EntityEquipmentSlot equipmentSlot, ItemStack heldItem) {
        super(inventoryPlayer, world);

        this.itemHandler = new ItemHandlerBase(ItemDeepLearner.getContainedItems(heldItem));
        this.deepLearnerSlot = inventoryPlayer.currentItem + DMLConstants.DeepLearner.INTERNAL_SLOTS;
        this.deepLearner = heldItem;
        this.equipmentSlot = equipmentSlot;

        addDataModelSlots();
        addInventorySlots(inventoryPlayer, 89, 153);
    }

    private void addDataModelSlots() {
        addSlotToContainer(new SlotDeepLearner(itemHandler, 0, 257, 100));
        addSlotToContainer(new SlotDeepLearner(itemHandler, 1, 275, 100));
        addSlotToContainer(new SlotDeepLearner(itemHandler, 2, 257, 118));
        addSlotToContainer(new SlotDeepLearner(itemHandler, 3, 275, 118));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack result = super.transferStackInSlot(playerIn, index);
        updateInventories();
        return result;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (((slotId == deepLearnerSlot) && !equipmentSlot.getName().equals("offhand"))
                || ((clickTypeIn == ClickType.SWAP) && (dragType == player.inventory.currentItem))) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
        updateInventories();
        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        updateInventories();
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }

    private void updateInventories() {
        ItemDeepLearner.setContainedItems(deepLearner, itemHandler.getItemStacks());
        ItemStack hand = inventoryPlayer.player.getItemStackFromSlot(equipmentSlot);
        if (!hand.isEmpty() && !hand.equals(deepLearner)) {
            inventoryPlayer.player.setItemStackToSlot(equipmentSlot, deepLearner);
        }
        inventoryPlayer.markDirty();
    }
}
