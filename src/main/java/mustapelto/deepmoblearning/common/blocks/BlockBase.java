package mustapelto.deepmoblearning.common.blocks;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public abstract class BlockBase extends Block {
    /**
     * @param name Block id (for internal use)
     * @param material Material the block behaves like
     */
    protected BlockBase(String name, Material material) {
        super(material);
        setRegistryName(name);
        setTranslationKey(DMLConstants.ModInfo.ID + "." + name);
        setCreativeTab(DMLRelearned.creativeTab);
        setLightLevel(1f);
    }

    public Optional<Item> getItemBlock() {
        ResourceLocation registryName = getRegistryName();
        return (registryName != null) ?
                Optional.of(new ItemBlock(this).setRegistryName(registryName)) :
                Optional.empty();
    }
}
