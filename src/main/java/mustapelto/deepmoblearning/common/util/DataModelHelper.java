package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
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
import java.util.HashMap;

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

    public static int getCurrentTierDataCount(ItemStack stack) {
        if (NBTHelper.hasKey(stack, "simulationCount") || NBTHelper.hasKey(stack, "killCount")) {
            // Update DeepMobLearning NBT to DMLRelearned format
            // i.e. "simulationCount" and "killCount" combined to a single value "dataCount"
            int currentSimulations = NBTHelper.getInt(stack, "simulationCount", 0);
            int currentKills = NBTHelper.getInt(stack, "killCount", 0);

            NBTHelper.removeKey(stack, "simulationCount");
            NBTHelper.removeKey(stack, "killCount");

            DataModelTierData tierData = getTierData(stack);
            if (tierData == null)
                return 0;

            NBTHelper.setInt(stack, "dataCount", currentSimulations + currentKills * tierData.getKillMultiplier());
        }
        return NBTHelper.getInt(stack, "dataCount", 0);
    }

    public static void setCurrentTierDataCount(ItemStack stack, int data) {
        NBTHelper.setInt(stack, "dataCount", data);
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
        // getMaxLevel() returns number of tiers, but tier index starts at 0 so we need to adjust for that
        // also true if "over max" (in case config has been changed on a running world to include fewer tiers)
        return getTierLevel(stack) >= DataModelTierDataManager.getMaxLevel() - 1;
    }

    public static boolean canSimulate(ItemStack stack) {
        // Can this model be run in a simulation chamber?
        DataModelTierData data = getTierData(stack);
        return data != null && data.getCanSimulate();
    }

    public static String getTierDisplayNameFormatted(ItemStack stack) {
        DataModelTierData data = getTierData(stack);
        return (data != null) ? data.getDisplayNameFormatted() : "";
    }

    public static String getTierDisplayNameFormatted(ItemStack stack, String template) {
        DataModelTierData data = getTierData(stack);
        return (data != null) ? data.getDisplayNameFormatted(template) : "";
    }

    public static String getNextTierDisplayNameFormatted(ItemStack stack) {
        DataModelTierData data = getNextTierData(stack);
        return (data != null) ? data.getDisplayNameFormatted() : "";
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
        int dataCurrent = getCurrentTierDataCount(stack);
        int killMultiplier = getTierKillMultiplier(stack);
        return isAtMaxTier(stack) ? 0 : MathHelper.DivideAndRoundUp(dataRequired - dataCurrent, killMultiplier);
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
    public static void increaseDataCount(ItemStack stack, EntityPlayerMP player, boolean isKill) {
        DataModelTierData tierData = getTierData(stack);
        if (tierData == null)
            return;

        int currentData = getCurrentTierDataCount(stack);

        // TODO: Glitch Sword and Trial stuff

        // Update data count and set NBT
        currentData += isKill ? tierData.getKillMultiplier() : 1; // TODO: *2 if glitch sword equipped and no trial active
        setCurrentTierDataCount(stack, currentData);

        // Update appropriate total count
        if (isKill)
            setTotalKillCount(stack, getTotalKillCount(stack) + 1);
        else
            setTotalSimulationCount(stack, getTotalSimulationCount(stack) + 1);

        if (tryIncreaseTier(stack)) {
            player.sendMessage(new TextComponentString(
                    I18n.format("deepmoblearning.data_model.reached_tier",
                            stack.getDisplayName(),
                            getTierDisplayNameFormatted(stack))
            ));
        }
    }

    /**
     * Increase tier of data model if current data has reached required data
     *
     * @param stack DataModel stack to process
     * @return true if tier could be increased, otherwise false
     */
    private static boolean tryIncreaseTier(ItemStack stack) {
        int tier = getTierLevel(stack);
        if (tier >= DMLConstants.DataModel.MAX_TIER)
            return false;

        int currentData = getCurrentTierDataCount(stack);
        int requiredData = getTierRequiredData(stack);

        if (currentData >= requiredData) {
            setCurrentTierDataCount(stack, currentData - requiredData); // extra data carries over to higher tier
            setTierLevel(stack, tier + 1);

            return true;
        }

        return false;
    }

    //
    // Inventory Data Manipulation (e.g. Creative Model Learner)
    //
    public static void findAndLevelUpModels(NonNullList<ItemStack> inventory, EntityPlayerMP player, CreativeLevelUpAction action) {
        for (ItemStack inventoryStack : inventory) {
            if (inventoryStack.getItem() instanceof ItemDeepLearner) {
                NonNullList<ItemStack> deepLearnerContents = ItemDeepLearner.getContainedItems(inventoryStack);
                for (ItemStack modelStack : deepLearnerContents) {
                    if (modelStack.getItem() instanceof ItemDataModel) {
                        int tier = getTierLevel(modelStack);
                        switch (action) {
                            case DECREASE_TIER:
                                if (tier > 0)
                                    setTierLevel(modelStack, tier - 1);
                                break;
                            case INCREASE_TIER:
                                if (!isAtMaxTier(modelStack))
                                    setTierLevel(modelStack, tier + 1);
                                break;
                            case INCREASE_KILLS:
                                if (!isAtMaxTier(modelStack))
                                    increaseDataCount(modelStack, player, true);
                        }
                    }
                }
                ItemDeepLearner.setContainedItems(inventoryStack, deepLearnerContents);
            }
        }
    }

    public enum CreativeLevelUpAction {
        INCREASE_TIER(0),
        INCREASE_KILLS(1),
        DECREASE_TIER(2);

        private final int value;
        private static final HashMap<Integer, CreativeLevelUpAction> map = new HashMap<>();

        CreativeLevelUpAction(int value) {
            this.value = value;
        }

        static {
            for (CreativeLevelUpAction creativeLevelUpAction : CreativeLevelUpAction.values())
                map.put(creativeLevelUpAction.value, creativeLevelUpAction);
        }

        public int toInt() {
            return value;
        }

        public static CreativeLevelUpAction fromInt(int value) {
            return map.get(value);
        }
    }
}
