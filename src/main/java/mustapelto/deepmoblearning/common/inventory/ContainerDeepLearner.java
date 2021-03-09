package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.util.PlayerHelper;
import mustapelto.deepmoblearning.common.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import static mustapelto.deepmoblearning.DMLConstants.Gui.DeepLearner.DATA_MODEL_SLOTS;
import static mustapelto.deepmoblearning.DMLConstants.Gui.DeepLearner.PLAYER_INVENTORY;

public class ContainerDeepLearner extends ContainerBase {
    public static final int INTERNAL_SLOTS = DATA_MODEL_SLOTS.size();
    private final ItemHandlerDataModel itemHandler;

    private final int deepLearnerSlotIndex; // inventory slot to make inaccessible while GUI open
    private final ItemStack deepLearner;

    public ContainerDeepLearner(EntityPlayer player) {
        deepLearner = PlayerHelper.getHeldDeepLearner(player);
        if (deepLearner.isEmpty())
            throw new IllegalArgumentException("Tried to open Deep Learner GUI without Deep Learner equipped");

        itemHandler = new ItemHandlerDataModel(ItemDeepLearner.getContainedItems(deepLearner));
        final int dlSlot = ((ItemDeepLearner) deepLearner.getItem()).getInventorySlot();

        deepLearnerSlotIndex = (dlSlot >= 0) ? dlSlot + INTERNAL_SLOTS : dlSlot;

        addDataModelSlots();
        addInventorySlots(player.inventory, PLAYER_INVENTORY.X, PLAYER_INVENTORY.Y);
    }

    private void addDataModelSlots() {
        for (int i = 0; i < INTERNAL_SLOTS; i++) {
            Point coords = DATA_MODEL_SLOTS.get(i);
            addSlotToContainer(new SlotDataModel(itemHandler, i, coords.X, coords.Y));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack result = super.transferStackInSlot(playerIn, index);
        ItemDeepLearner.setContainedItems(deepLearner, itemHandler.getItemStacks());
        playerIn.inventory.markDirty();
        return result;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        // Prevent moving Deep Learner while it is open
        if (((deepLearnerSlotIndex != -1) && (slotId == deepLearnerSlotIndex))
                || ((clickTypeIn == ClickType.SWAP) && (dragType == player.inventory.currentItem))) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
        ItemDeepLearner.setContainedItems(deepLearner, itemHandler.getItemStacks());
        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        ItemDeepLearner.setContainedItems(deepLearner, itemHandler.getItemStacks());
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }
}
