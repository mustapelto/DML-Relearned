package mustapelto.deepmoblearning.common.blocks;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class DMLBlock extends Block {
    /**
     * @param name Block id (for internal use)
     * @param material Material the block behaves like
     */
    public DMLBlock(String name, Material material) {
        super(material);
        setRegistryName(name);
        setUnlocalizedName(DMLConstants.ModInfo.ID + "." + name);
        setCreativeTab(DMLRelearned.creativeTab);
        setLightLevel(1f);
    }
}
