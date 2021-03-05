package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.common.items.*;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemStackHelper {
    public static boolean isDataModel(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemDataModel;
    }

    public static boolean isDeepLearner(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemDeepLearner;
    }

    public static boolean isPolymerClay(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemPolymerClay;
    }

    public static boolean isPristineMatter(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemPristineMatter;
    }

    public static boolean isLivingMatter(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemLivingMatter;
    }

    public static boolean isTrialKey(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemTrialKey;
    }

    public static boolean isGlitchSword(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemGlitchSword;
    }

    public static boolean isGlitchArmor(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemGlitchArmor;
    }

    public static boolean isCreativeModelLearner(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemCreativeModelLearner;
    }
}
