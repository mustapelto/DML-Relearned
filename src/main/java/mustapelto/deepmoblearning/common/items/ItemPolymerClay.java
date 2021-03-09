package mustapelto.deepmoblearning.common.items;

import net.minecraft.item.ItemStack;

public class ItemPolymerClay extends ItemBase {
    public ItemPolymerClay() {
        super("polymer_clay", 64);
    }

    public static boolean isPolymerClay(ItemStack stack) {
        return stack.getItem() instanceof ItemPolymerClay;
    }
}
