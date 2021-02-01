package mustapelto.deepmoblearning.common.blocks;

import net.minecraft.block.material.Material;

public class BlockInfusedIngot extends BlockBase {
    public BlockInfusedIngot() {
        super("infused_ingot_block", Material.IRON);
        setHardness(4f);
        setResistance(10f);
    }
}
