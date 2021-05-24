package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.util.StringHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemTrialKey extends ItemBase {
    public ItemTrialKey() {
        super("trial_key", 1);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StringHelper.getFormattedString(super.getItemStackDisplayName(stack), TextFormatting.AQUA);
    }
}
