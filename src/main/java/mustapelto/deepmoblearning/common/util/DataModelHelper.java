package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.metadata.DataModelTierData;
import mustapelto.deepmoblearning.common.metadata.DataModelTierDataManager;
import mustapelto.deepmoblearning.common.metadata.MobMetaData;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;

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

    @Nullable
    public static ItemDataModel getDataModelItem(ItemStack stack) {
        Item stackItem = stack.getItem();
        return stackItem instanceof ItemDataModel ? (ItemDataModel) stackItem : null;
    }

    @Nullable
    public static MobMetaData getMobMetaData(ItemStack stack) {
        ItemDataModel stackItem = getDataModelItem(stack);
        return stackItem != null ? stackItem.getMobMetaData() : null;
    }

    @Nullable
    private static DataModelTierData getTierData(ItemStack stack) {
        return DataModelTierDataManager.getByLevel(getTierLevel(stack));
    }

    @Nullable
    private static DataModelTierData getNextTierData(ItemStack stack) {
        return DataModelTierDataManager.getByLevel(getTierLevel(stack) + 1);
    }

    public static boolean isAtMaxTier(ItemStack stack) {
        return getTierLevel(stack) == DataModelTierDataManager.getMaxLevel();
    }

    public static String getTierDisplayNameFormatted(ItemStack stack) {
        DataModelTierData data = getTierData(stack);
        return (data != null) ? data.getDisplayNameFormatted() : "";
    }

    public static String getNextTierDisplayNameFormatted(ItemStack stack) {
        DataModelTierData data = getNextTierData(stack);
        return (data != null) ? data.getDisplayNameFormatted() : "";
    }

    public static int getTierCurrentData(ItemStack stack) {
        int killCount = getCurrentTierKillCount(stack);
        int simulationCount = getCurrentTierSimulationCount(stack);
        int killMultiplier = getTierKillMultiplier(stack);
        return isAtMaxTier(stack) ? 0
                : simulationCount + killCount * killMultiplier;
    }

    public static int getTierRequiredData(ItemStack stack) {
        DataModelTierData data = getTierData(stack);
        return (data != null) ? data.getDataToNext() : 0;
    }

    public static int getTierKillMultiplier(ItemStack stack) {
        DataModelTierData data = getTierData(stack);
        return (data != null) ? data.getKillMultiplier() : 0;
    }

    public static int getKillsToNextTier(ItemStack stack) {
        int dataRequired = getTierRequiredData(stack);
        int currentData = getTierCurrentData(stack);
        int killMultiplier = getTierKillMultiplier(stack);
        return isAtMaxTier(stack) ? 0 : MathHelper.DivideAndRoundUp(dataRequired - currentData, killMultiplier);
    }

    // Filter out non-data model stacks and return filtered list
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

        if (getTierCurrentData(stack) >= getTierRequiredData(stack)) {
            setCurrentTierKillCount(stack, 0);
            setCurrentTierSimulationCount(stack, 0);
            setTierLevel(stack, tier + 1);

            return true;
        }

        return false;
    }
}
