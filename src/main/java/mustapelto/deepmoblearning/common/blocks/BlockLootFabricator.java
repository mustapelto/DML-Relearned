package mustapelto.deepmoblearning.common.blocks;

import mustapelto.deepmoblearning.common.tiles.TileEntityLootFabricator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockLootFabricator extends BlockMachine {
    public BlockLootFabricator() {
        // Original mod used "extraction chamber" as registry name for this block
        // so this was kept for backwards compatibility
        super("extraction_chamber");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityLootFabricator();
    }

    @Override
    public Class<TileEntityLootFabricator> getTileEntityClass() {
        return TileEntityLootFabricator.class;
    }
}
