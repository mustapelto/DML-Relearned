package mustapelto.deepmoblearning.common.blocks;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.tiles.TileEntityMachine;
import mustapelto.deepmoblearning.common.tiles.CraftingState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk;

public abstract class BlockMachine extends BlockTileEntity {
    private static final PropertyEnum<CraftingState> CRAFTING_STATE = PropertyEnum.create("state", CraftingState.class);

    public BlockMachine(String name) {
        super(name, Material.ROCK, DMLConstants.Gui.IDs.MACHINE);
        setHardness(4f);
        setResistance(10f);
        setDefaultState(super.getDefaultState().withProperty(CRAFTING_STATE, CraftingState.IDLE));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, CRAFTING_STATE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te;
        if (worldIn instanceof ChunkCache) {
            te = ((ChunkCache) worldIn).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        } else {
            te = worldIn.getTileEntity(pos);
        }

        if (!(te instanceof TileEntityMachine))
            return state;

        return state.withProperty(CRAFTING_STATE, ((TileEntityMachine) te).getCraftingState());
    }
}
