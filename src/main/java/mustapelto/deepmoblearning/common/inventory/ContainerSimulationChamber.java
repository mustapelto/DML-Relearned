package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

import static mustapelto.deepmoblearning.client.gui.GuiSimulationChamber.*;

public class ContainerSimulationChamber extends ContainerMachine {
    public ContainerSimulationChamber(TileEntitySimulationChamber tileEntity, InventoryPlayer inventoryPlayer) {
        super(tileEntity, inventoryPlayer, PLAYER_INVENTORY.X, PLAYER_INVENTORY.Y);
    }

    @Override
    protected void addMachineInventory(IItemHandler inventory) {
        addSlotToContainer(new SlotDataModel(inventory, 0, DATA_MODEL_SLOT.LEFT + 1, DATA_MODEL_SLOT.TOP + 1));
        addSlotToContainer(new SlotPolymerClay(inventory, 1, POLYMER_SLOT.X, POLYMER_SLOT.Y));
        addSlotToContainer(new SlotOutput(inventory, 2, LIVING_MATTER_SLOT.X, LIVING_MATTER_SLOT.Y)); // Living Matter output
        addSlotToContainer(new SlotOutput(inventory, 3, PRISTINE_MATTER_SLOT.X, PRISTINE_MATTER_SLOT.Y)); // Pristine Matter output
    }
}
