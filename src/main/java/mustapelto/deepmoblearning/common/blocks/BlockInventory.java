package mustapelto.deepmoblearning.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class BlockInventory<TE extends TileEntity> extends BlockTileEntity<TE> {

    /**
     * @param name     Block id (for internal use)
     * @param material Material the block behaves like
     */
    protected BlockInventory(String name, Material material) {
        super(name, material);
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileEntity = Objects.requireNonNull(worldIn.getTileEntity(pos));
        IItemHandler inventory = Objects.requireNonNull(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                EntityItem item = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
                item.setDefaultPickupDelay();
                worldIn.spawnEntity(item);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }
}
