package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerDeepLearner extends ContainerBase {
    public static final int INTERNAL_SLOTS = 4;
    private final ItemHandlerBase itemHandler;

    private final int deepLearnerSlotIndex; // inventory slot to make inaccessible while GUI open
    private final ItemStack deepLearner;

    public ContainerDeepLearner(EntityPlayer player) {
        final ItemStack mainHand = player.getHeldItemMainhand();
        final ItemStack offHand = player.getHeldItemOffhand();
        if (mainHand.getItem() instanceof ItemDeepLearner)
            deepLearner = mainHand;
        else if (offHand.getItem() instanceof ItemDeepLearner)
            deepLearner = offHand;
        else
            throw new IllegalArgumentException("Tried to open Deep Learner GUI without Deep Learner equipped");
        itemHandler = new ItemHandlerBase(ItemDeepLearner.getContainedItems(deepLearner));
        final int dlSlot = ((ItemDeepLearner) deepLearner.getItem()).getInventorySlot();

        deepLearnerSlotIndex = (dlSlot >= 0) ? dlSlot + INTERNAL_SLOTS : dlSlot;

        addDataModelSlots();
        addInventorySlots(player.inventory, 89, 153);
    }

    private void addDataModelSlots() {
        addSlotToContainer(new SlotDataModel(itemHandler, 0, 257, 100));
        addSlotToContainer(new SlotDataModel(itemHandler, 1, 275, 100));
        addSlotToContainer(new SlotDataModel(itemHandler, 2, 257, 118));
        addSlotToContainer(new SlotDataModel(itemHandler, 3, 275, 118));
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
        ItemStack result = super.transferStackInSlot(playerIn, index);
        ItemDeepLearner.setContainedItems(deepLearner, itemHandler.getItemStacks());
        return result;
    }

    @Override
    @Nonnull
    public ItemStack slotClick(int slotId, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull EntityPlayer player) {
        DMLRelearned.logger.info("Slot: {} - DLIndex: {} - DragType: {} - ClickType: {}", slotId, deepLearnerSlotIndex, dragType, clickTypeIn);
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
    public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        ItemDeepLearner.setContainedItems(deepLearner, itemHandler.getItemStacks());
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }
}
