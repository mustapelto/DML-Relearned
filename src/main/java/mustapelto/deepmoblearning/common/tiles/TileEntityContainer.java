package mustapelto.deepmoblearning.common.tiles;

import mustapelto.deepmoblearning.common.inventory.ContainerTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public abstract class TileEntityContainer extends TileEntityBase {
    protected static final String NBT_INVENTORY = "inventory"; // Inventory contents subtag (only used by subclasses)

    //
    // Inventory
    //

    public abstract ContainerTileEntity getContainer(InventoryPlayer inventoryPlayer);
}
