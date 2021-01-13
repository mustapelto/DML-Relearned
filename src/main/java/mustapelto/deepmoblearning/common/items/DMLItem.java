package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.item.Item;

public abstract class DMLItem extends Item {
    public DMLItem(String name, int stackSize) {
        setRegistryName(name);
        setUnlocalizedName(DMLConstants.ModInfo.ID + "." + name);
        setMaxStackSize(stackSize);
        setCreativeTab(DMLRelearned.creativeTab);
    }
}
