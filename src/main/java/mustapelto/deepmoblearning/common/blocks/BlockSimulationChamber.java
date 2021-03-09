package mustapelto.deepmoblearning.common.blocks;

import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockSimulationChamber extends BlockMachine {
    public BlockSimulationChamber() {
        super("simulation_chamber");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySimulationChamber();
    }
}
