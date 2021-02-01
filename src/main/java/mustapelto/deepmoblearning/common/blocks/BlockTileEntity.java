package mustapelto.deepmoblearning.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockTileEntity<TE extends TileEntity> extends BlockFacing {
    /**
     * @param name     Block id (for internal use)
     * @param material Material the block behaves like
     */
    protected BlockTileEntity(String name, Material material) {
        super(name, material);
    }

    @SuppressWarnings("unchecked")
    public TE getTileEntity(IBlockAccess world, BlockPos pos) {
        TileEntity entity = world.getTileEntity(pos);
        return (TE) world.getTileEntity(pos);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state);

    public abstract Class<TE> getTileEntityClass();
}
