package mustapelto.deepmoblearning.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMachineCasing extends BlockBase {
    public BlockMachineCasing() {
        super("machine_casing", Material.ROCK);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return false;
    }
}
