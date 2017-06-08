package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.item.Item;

public abstract class ItemBase extends Item {
    public ItemBase(String name, int stackSize) {
        setRegistryName(name);
        setUnlocalizedName(DMLConstants.ModInfo.ID + "." + name);
        setMaxStackSize(stackSize);
        setCreativeTab(DMLRelearned.creativeTab);
    }
}
