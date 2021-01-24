package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.item.Item;

public abstract class DMLItem extends Item {
    /**
     * @param name Item id (for internal use)
     * @param stackSize Item max stack size
     * @param addToCreative Should this item be added to creative tab?
     */
    public DMLItem(String name, int stackSize, boolean addToCreative) {
        setRegistryName(name);
        setUnlocalizedName(DMLConstants.ModInfo.ID + "." + name);
        setMaxStackSize(stackSize);
        if (addToCreative) // only add item to creative tab if its associated mod is loaded (checked by subclass)
            setCreativeTab(DMLRelearned.creativeTab);
    }

    /**
     * Short-form constructor for items not dependent on other mods (these should always be added to the creative tab)
     *
     * @param name Item id (for internal use)
     * @param stackSize Item max stack size
     */
    public DMLItem(String name, int stackSize) {
        this(name, stackSize, true);
    }
}
