package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.tiles.TileEntityMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public abstract class ContainerMachine extends ContainerBase {
    private final TileEntityMachine tileEntity;

    public ContainerMachine(TileEntityMachine tileEntity, InventoryPlayer inventoryPlayer, int playerInventoryX, int playerInventoryY) {
        this.tileEntity = tileEntity;

        IItemHandler inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        addMachineInventory(inventory);

        addInventorySlots(inventoryPlayer, playerInventoryX, playerInventoryY);
    }

    protected abstract void addMachineInventory(IItemHandler inventory);

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
        ItemStack result = super.transferStackInSlot(playerIn, index);
        tileEntity.markDirty();
        playerIn.inventory.markDirty(); // Not sure if this is necessary
        return result;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        BlockPos pos = tileEntity.getPos();

        // Don't allow player interaction if tileEntity has changed (e.g. been removed)
        // or if player is spectator
        if (tileEntity.getWorld().getTileEntity(pos) != tileEntity || playerIn.isSpectator())
            return false;

        // Don't allow player interaction from far away
        return playerIn.getDistanceSqToCenter(pos) <= 64;
    }
}
