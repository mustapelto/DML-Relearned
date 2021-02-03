package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

import static mustapelto.deepmoblearning.client.gui.SimulationChamberGui.*;

public class ContainerSimulationChamber extends ContainerBase {
    private final TileEntitySimulationChamber tileEntity;

    public ContainerSimulationChamber(TileEntitySimulationChamber tileEntity, InventoryPlayer inventoryPlayer) {
        this.tileEntity = tileEntity;

        IItemHandler inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // Add internal slots
        addSlotToContainer(new SlotDataModel(inventory, 0, DATA_MODEL_SLOT.LEFT + 1, DATA_MODEL_SLOT.TOP + 1));
        addSlotToContainer(new SlotPolymerClay(inventory, 1, POLYMER_SLOT.LEFT, POLYMER_SLOT.TOP));
        addSlotToContainer(new SlotOutput(inventory, 2, LIVING_MATTER_SLOT.LEFT, LIVING_MATTER_SLOT.TOP)); // Living Matter output
        addSlotToContainer(new SlotOutput(inventory, 3, PRISTINE_MATTER_SLOT.LEFT, PRISTINE_MATTER_SLOT.TOP)); // Pristine Matter output

        addInventorySlots(inventoryPlayer, 36, 153);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
        ItemStack result = super.transferStackInSlot(playerIn, index);
        tileEntity.markDirty();
        playerIn.inventory.markDirty();
        return result;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        BlockPos pos = tileEntity.getPos();
        if (tileEntity.getWorld().getTileEntity(pos) != tileEntity || playerIn.isSpectator())
            return false; // Checks to prevent illegal access
        return playerIn.getDistanceSqToCenter(pos) <= 64; // prevent "long distance" interaction
    }
}
