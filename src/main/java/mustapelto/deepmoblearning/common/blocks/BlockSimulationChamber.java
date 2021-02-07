package mustapelto.deepmoblearning.common.blocks;

import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockSimulationChamber extends BlockMachine {
    public BlockSimulationChamber() {
        super("simulation_chamber");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntitySimulationChamber();
    }

    @Override
    public Class<TileEntitySimulationChamber> getTileEntityClass() {
        return TileEntitySimulationChamber.class;
    }
}
