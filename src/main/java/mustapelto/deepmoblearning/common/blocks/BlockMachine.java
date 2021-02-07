package mustapelto.deepmoblearning.common.blocks;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.block.material.Material;

public abstract class BlockMachine extends BlockTileEntity {
    public BlockMachine(String name) {
        super(name, Material.ROCK, DMLConstants.Gui.IDs.MACHINE);
        setHardness(4f);
        setResistance(10f);
    }
}
