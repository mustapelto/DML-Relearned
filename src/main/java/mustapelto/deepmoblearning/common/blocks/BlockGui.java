package mustapelto.deepmoblearning.common.blocks;

import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class BlockGui<TE extends TileEntity> extends BlockInventory<TE> {
    private final int GUI_ID;

    /**
     * @param name     Block id (for internal use)
     * @param material Material the block behaves like
     */
    protected BlockGui(String name, Material material, int guiId) {
        super(name, material);
        GUI_ID = guiId;
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (!playerIn.isSneaking()) {
                playerIn.openGui(DMLRelearned.instance, GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
}
