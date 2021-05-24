package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.common.items.*;
import net.minecraft.item.ItemStack;

public class ItemStackHelper {
    public static boolean isDataModel(ItemStack stack) {
        return stack.getItem() instanceof ItemDataModel;
    }

    public static boolean isDeepLearner(ItemStack stack) {
        return stack.getItem() instanceof ItemDeepLearner;
    }

    public static boolean isPolymerClay(ItemStack stack) {
        return stack.getItem() instanceof ItemPolymerClay;
    }

    public static boolean isPristineMatter(ItemStack stack) {
        return stack.getItem() instanceof ItemPristineMatter;
    }

    public static boolean isLivingMatter(ItemStack stack) {
        return stack.getItem() instanceof ItemLivingMatter;
    }

    public static boolean isGlitchSword(ItemStack stack) {
        return stack.getItem() instanceof ItemGlitchSword;
    }

    public static boolean isGlitchArmor(ItemStack stack) {
        return stack.getItem() instanceof ItemGlitchArmor;
    }

    public static boolean isCreativeModelLearner(ItemStack stack) {
        return stack.getItem() instanceof ItemCreativeModelLearner;
    }
}
