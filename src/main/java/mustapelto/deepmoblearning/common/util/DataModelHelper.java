package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLConfig;
import mustapelto.deepmoblearning.common.enums.EnumDataModelTier;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.metadata.MobMetaData;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;

public class DataModelHelper {
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

    public static int getTotalKillCount(ItemStack stack) {
        return NBTHelper.getInt(stack, "totalKillCount", 0);
    }

    public static void setTotalKillCount(ItemStack stack, int count) {
        NBTHelper.setInt(stack, "totalKillCount", count);
    }

    public static int getTotalSimulationCount(ItemStack stack) {
        return NBTHelper.getInt(stack, "totalSimulationCount", 0);
    }

    public static void setTotalSimulationCount(ItemStack stack, int count) {
        NBTHelper.setInt(stack, "totalSimulationCount", count);
    }

    //
    // Calculated Getters
    //

    public static MobMetaData getMobMetaData(ItemStack stack) {
        Item stackItem = stack.getItem();
        return stackItem instanceof ItemDataModel ? ((ItemDataModel) stackItem).getMobMetaData() : null;
    }

    private static EnumDataModelTier getTier(ItemStack stack) {
        return EnumDataModelTier.fromLevel(getTierLevel(stack));
    }

    private static EnumDataModelTier getNextTier(ItemStack stack) {
        return EnumDataModelTier.fromLevel(getTierLevel(stack) + 1);
    }

    public static String getTierDisplayNameFormatted(ItemStack stack) {
        return getTier(stack).getDisplayNameFormatted();
    }

    public static String getNextTierDisplayNameFormatted(ItemStack stack) {
        return getNextTier(stack).getDisplayNameFormatted();
    }

    public static boolean isAtMaxTier(ItemStack stack) {
        return getTier(stack).getLevel() == DMLConstants.DataModel.MAX_TIER;
    }

    public static int getCurrentTierCurrentData(ItemStack stack) {
        int killCount = getCurrentTierKillCount(stack);
        int simulationCount = getCurrentTierSimulationCount(stack);
        int killMultiplier = getCurrentTierKillMultiplier(stack);
        return isAtMaxTier(stack) ? 0
                : simulationCount + killCount * killMultiplier;
    }

    public static int getCurrentTierRequiredData(ItemStack stack) {
        return DMLConfig.DATA_MODEL_EXPERIENCE_TWEAKS.getDataRequired(getTier(stack));
    }

    public static int getCurrentTierKillMultiplier(ItemStack stack) {
        return DMLConfig.DATA_MODEL_EXPERIENCE_TWEAKS.getKillMultiplier(getTier(stack));
    }

    // total data required for next tier
    private static int getTierMaxData(ItemStack stack) {
        return isAtMaxTier(stack) ? 0 : getCurrentTierRequiredData(stack);
    }

    // total kills required for next tier
    private static int getTierMaxKills(ItemStack stack) {
        int dataRequired = getCurrentTierRequiredData(stack);
        int killMultiplier = getCurrentTierKillMultiplier(stack);
        return isAtMaxTier(stack) ? 0 : MathHelper.DivideAndRoundUp(dataRequired, killMultiplier);
    }

    public static int getKillsToNextTier(ItemStack stack) {
        int dataRequired = getCurrentTierRequiredData(stack);
        int currentData = getCurrentTierCurrentData(stack);
        int killMultiplier = getCurrentTierKillMultiplier(stack);
        return isAtMaxTier(stack) ? 0 : MathHelper.DivideAndRoundUp(dataRequired - currentData, killMultiplier);
    }

    public static NonNullList<ItemStack> getDataModelStacksFromList(NonNullList<ItemStack> stackList) {
        NonNullList<ItemStack> result = NonNullList.create();

        stackList.forEach(stack -> {
            if (stack.getItem() instanceof ItemDataModel)
                result.add(stack);
        });

        return result;
    }

    //
    // Data Manipulation
    //
    public static void increaseKillCount(ItemStack stack, EntityPlayerMP player) {
        int tier = getTierLevel(stack);
        int currentKillCount = getCurrentTierKillCount(stack);

        // TODO: Glitch Sword and Trial Stuff

        // Update kill count and set NBT
        currentKillCount += /*(isGlitchSwordEquipped && !cap.isTrialActive() ? 2 : */1;
        setCurrentTierKillCount(stack, currentKillCount);
        setTotalKillCount(stack, getTotalKillCount(stack) + 1);

        if (tryIncreaseTier(stack)) {
            player.sendMessage(new TextComponentString(
                    I18n.format("deepmoblearning.data_model.reached_tier",
                            stack.getDisplayName(),
                            getTierDisplayNameFormatted(stack))
            ));
        }
    }

    private static boolean tryIncreaseTier(ItemStack stack) {
        int tier = getTierLevel(stack);
        if (tier >= DMLConstants.DataModel.MAX_TIER)
            return false;

        if (getCurrentTierCurrentData(stack) >= getCurrentTierRequiredData(stack)) {
            setCurrentTierKillCount(stack, 0);
            setCurrentTierSimulationCount(stack, 0);
            setTierLevel(stack, tier + 1);

            return true;
        }

        return false;
    }
}
