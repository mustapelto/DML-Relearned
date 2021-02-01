package mustapelto.deepmoblearning.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockMachineCasing extends BlockBase {
    public BlockMachineCasing() {
        super("machine_casing", Material.ROCK);
    }

    @Override
    public boolean canPlaceBlockAt(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        return false;
    }
}
