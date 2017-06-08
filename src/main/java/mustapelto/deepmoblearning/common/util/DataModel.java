package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.enums.EnumDataModelTier;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.enums.EnumMobType;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DataModel {
    //
    // NBT Getters/Setters
    //

    public static int getTierLevel(ItemStack stack) {
        return NBTHelper.getInt(stack, "tier", 0);
    }

    public static void setTierLevel(ItemStack stack, int tier) {
        NBTHelper.setInt(stack, "tier", tier);
    }

    public static int getCurrentTierKillCount(ItemStack stack) {
        return NBTHelper.getInt(stack, "killCount", 0);
    }

    public static void setCurrentTierKillCount(ItemStack stack, int count) {
        NBTHelper.setInt(stack, "killCount", count);
    }

    public static int getCurrentTierSimulationCount(ItemStack stack) {
        return NBTHelper.getInt(stack, "simulationCount", 0);
    }

    public static void setCurrentTierSimulationCount(ItemStack stack, int count) {
        NBTHelper.setInt(stack, "simulationCount", count);
    }

    //
    // Calculated Getters
    //

    private static EnumMobType getMobType(ItemStack stack) {
        Item stackItem = stack.getItem();
        return stackItem instanceof ItemDataModel ? ((ItemDataModel) stackItem).getMobType() : null;
    }

    public static String getExtraTooltip(ItemStack stack) {
        EnumMobType stackMobType = getMobType(stack);
        return stackMobType != null ? stackMobType.getExtraTooltip() : "";
    }

    private static EnumDataModelTier getTier(ItemStack stack) {
        return EnumDataModelTier.fromLevel(getTierLevel(stack));
    }

    public static String getTierDisplayName(ItemStack stack) {
        return getTier(stack).getDisplayName();
    }

    public static String getTierDisplayNameFormatted(ItemStack stack) {
        return getTier(stack).getDisplayNameFormatted();
    }

    public static int getCurrentTierCurrentData(ItemStack stack) {
        EnumDataModelTier tier = getTier(stack);
        int killCount = getCurrentTierKillCount(stack);
        int simulationCount = getCurrentTierSimulationCount(stack);
        if (tier.getLevel() == DMLConstants.DataModel.MAX_TIER) {
            return 0;
        }
        return simulationCount + killCount * DMLConfig.DATA_MODEL_EXPERIENCE_TWEAKS.getKillMultiplier(tier);
    }

    public static int getCurrentTierRequiredData(ItemStack stack) {
        return DMLConfig.DATA_MODEL_EXPERIENCE_TWEAKS.getKillsRequired(getTier(stack));
    }

    public static int getCurrentKillMultiplier(ItemStack stack) {
        return DMLConfig.DATA_MODEL_EXPERIENCE_TWEAKS.getKillMultiplier(getTier(stack));
    }

    public static int getCurrentTierSimulationCost(ItemStack stack) {
        EnumMobType stackMobType = getMobType(stack);
        return stackMobType != null ? stackMobType.getSimulationTickCost() : 0;
    }

    public static EnumLivingMatterType getLivingMatterType(ItemStack stack) {
        EnumMobType stackMobType = getMobType(stack);
        return stackMobType != null ? stackMobType.getLivingMatterType() : null;
    }

    public static String getLivingMatterDisplayNameFormatted(ItemStack stack) {
        EnumLivingMatterType livingMatterType = getLivingMatterType(stack);
        return livingMatterType != null ? livingMatterType.getDisplayNameFormatted() : "";
    }
}
