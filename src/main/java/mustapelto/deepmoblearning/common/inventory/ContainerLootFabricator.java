package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.tiles.TileEntityLootFabricator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

import static mustapelto.deepmoblearning.client.gui.GuiLootFabricator.*;

public class ContainerLootFabricator extends ContainerMachine {
    public ContainerLootFabricator(TileEntityLootFabricator tileEntity, InventoryPlayer inventoryPlayer) {
        super(tileEntity, inventoryPlayer, PLAYER_INVENTORY.X, PLAYER_INVENTORY.Y);
    }

    @Override
    protected void addMachineInventory(IItemHandler inventory) {
        addSlotToContainer(new SlotPristineMatter(inventory, 0, INPUT_SLOT.X, INPUT_SLOT.Y));

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int index = row * 4 + col + 1;
                int x = OUTPUT_FIRST_SLOT.X + OUTPUT_SLOT_SIDE_LENGTH * col;
                int y = OUTPUT_FIRST_SLOT.Y + OUTPUT_SLOT_SIDE_LENGTH * row;
                addSlotToContainer(new SlotOutput(inventory, index, x, y));
            }
        }
    }
}
