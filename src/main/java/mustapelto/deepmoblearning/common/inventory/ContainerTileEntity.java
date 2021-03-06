package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.tiles.TileEntityContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class ContainerTileEntity extends ContainerBase {
    private final TileEntityContainer tileEntity;

    public ContainerTileEntity(TileEntityContainer tileEntity, InventoryPlayer inventoryPlayer, int playerInventoryX, int playerInventoryY) {
        this.tileEntity = tileEntity;

        IItemHandler inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory == null)
            throw new NullPointerException("Couldn't retrieve inventory of tile entity at " + tileEntity.getPos().toString());

        addTileEntityInventory(inventory);

        addInventorySlots(inventoryPlayer, playerInventoryX, playerInventoryY);
    }

    protected abstract void addTileEntityInventory(IItemHandler inventory);

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack result = super.transferStackInSlot(playerIn, index);
        tileEntity.markDirty();
        playerIn.inventory.markDirty(); // Not sure if this is necessary
        return result;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        BlockPos pos = tileEntity.getPos();

        // Don't allow player interaction if tileEntity has changed (e.g. been removed)
        // or if player is spectator
        if (tileEntity.getWorld().getTileEntity(pos) != tileEntity || playerIn.isSpectator())
            return false;

        // Don't allow player interaction from far away
        return Math.sqrt(playerIn.getDistanceSqToCenter(pos)) < 8;
    }
}
