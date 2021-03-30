package mustapelto.deepmoblearning.common.inventory;

import mustapelto.deepmoblearning.common.tiles.TileEntityTrialKeystone;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

import static mustapelto.deepmoblearning.client.gui.GuiTrialKeystone.PLAYER_INVENTORY;
import static mustapelto.deepmoblearning.client.gui.GuiTrialKeystone.TRIAL_KEY_SLOT;

public class ContainerTrialKeystone extends ContainerTileEntity {
    public ContainerTrialKeystone(TileEntityTrialKeystone tileEntity, InventoryPlayer inventoryPlayer) {
        super(tileEntity, inventoryPlayer, PLAYER_INVENTORY.X, PLAYER_INVENTORY.Y);
    }

    @Override
    protected void addTileEntityInventory(IItemHandler inventory) {
        addSlotToContainer(new SlotTrialKey(inventory, 0, TRIAL_KEY_SLOT.LEFT + 1, TRIAL_KEY_SLOT.TOP + 1));
    }
}
