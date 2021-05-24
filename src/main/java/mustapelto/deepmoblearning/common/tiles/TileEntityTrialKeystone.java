package mustapelto.deepmoblearning.common.tiles;

import mustapelto.deepmoblearning.common.inventory.ContainerTileEntity;
import mustapelto.deepmoblearning.common.inventory.ContainerTrialKeystone;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityTrialKeystone extends TileEntityContainer implements ITickable {
    @Override
    public void update() {
    }

    @Override
    public ContainerTileEntity getContainer(InventoryPlayer inventoryPlayer) {
        return new ContainerTrialKeystone(this, inventoryPlayer);
    }

    //
    // RENDER
    //

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos(), getPos().add(1, 2, 1));
    }
}
