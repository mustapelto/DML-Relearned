package mustapelto.deepmoblearning.common.util;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModelTier;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModelTiers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Helper methods for Data Model ItemStacks
 */
public class DataModelHelper {
    //
    // ItemStack methods - NBT getters / setters
    //

    public static final String NBT_TIER = "tier";
    public static final String NBT_DATA_COUNT = "dataCount";
    public static final String NBT_TOTAL_KILL_COUNT = "totalKillCount";
    public static final String NBT_TOTAL_SIMULATION_COUNT = "totalSimulationCount";
    public static final String NBT_LEGACY_SIMULATION_COUNT = "simulationCount";
    public static final String NBT_LEGACY_KILL_COUNT = "killCount";

    private static boolean isLegacyNBT(ItemStack stack) {
        return NBTHelper.hasKey(stack, NBT_LEGACY_KILL_COUNT) ||
                NBTHelper.hasKey(stack, NBT_LEGACY_SIMULATION_COUNT);
    }

    private static void convertLegacyNBT(ItemStack stack) {
        int currentSimulations = NBTHelper.getInteger(stack, NBT_LEGACY_SIMULATION_COUNT);
        int currentKills = NBTHelper.getInteger(stack, NBT_LEGACY_KILL_COUNT);

        NBTHelper.removeKey(stack, NBT_LEGACY_SIMULATION_COUNT);
        NBTHelper.removeKey(stack, NBT_LEGACY_KILL_COUNT);

        Optional<MetadataDataModelTier> tierData = getTierData(stack);
        int killMultiplier = tierData.map(MetadataDataModelTier::getKillMultiplier).orElse(0);
        int currentData = currentSimulations + currentKills * killMultiplier;

        NBTHelper.setInteger(stack, NBT_DATA_COUNT, currentData);
    }

    public static int getTierLevel(ItemStack stack) {
        return ItemStackHelper.isDataModel(stack) ? NBTHelper.getInteger(stack, NBT_TIER) : 0;
    }

    public static void setTierLevel(ItemStack stack, int level) {
        NBTHelper.setInteger(stack, NBT_TIER, level);
    }

    public static int getCurrentTierDataCount(ItemStack stack) {
        if (isLegacyNBT(stack))
            convertLegacyNBT(stack);
        return NBTHelper.getInteger(stack, NBT_DATA_COUNT);
    }

    public static void setCurrentTierDataCount(ItemStack stack, int count) {
        NBTHelper.setInteger(stack, NBT_DATA_COUNT, count);
    }

    public static int getTotalKillCount(ItemStack stack) {
        return NBTHelper.getInteger(stack, NBT_TOTAL_KILL_COUNT);
    }

    public static void setTotalKillCount(ItemStack stack, int count) {
        NBTHelper.setInteger(stack, NBT_TOTAL_KILL_COUNT, count);
    }

    public static int getTotalSimulationCount(ItemStack stack) {
        return NBTHelper.getInteger(stack, NBT_TOTAL_SIMULATION_COUNT);
    }

    public static void setTotalSimulationCount(ItemStack stack, int count) {
        NBTHelper.setInteger(stack, NBT_TOTAL_SIMULATION_COUNT, count);
    }

    //
    // ItemStack methods - calculated getters
    //

    public static Optional<MetadataDataModel> getDataModelMetadata(ItemStack stack) {
        return (ItemStackHelper.isDataModel(stack)) ?
                Optional.of(((ItemDataModel)stack.getItem()).getDataModelMetadata()) :
                Optional.empty();
    }

    public static Optional<MetadataDataModelTier> getTierData(ItemStack stack) {
        int level = getTierLevel(stack);
        return MetadataManagerDataModelTiers.INSTANCE.getByLevel(level);
    }

    private static Optional<MetadataDataModelTier> getNextTierData(ItemStack stack) {
        int level = getTierLevel(stack) + 1;
        return MetadataManagerDataModelTiers.INSTANCE.getByLevel(level);
    }

    /**
     * Is this Data Model at or above max tier?
     * @param stack Data Model stack
     * @return true if Data Model tier is equal or greater than config-based max tier
     */
    public static boolean isMaxTier(ItemStack stack) {
        return getTierLevel(stack) >= MetadataManagerDataModelTiers.INSTANCE.getMaxLevel();
    }

    /**
     * Can this Data Model be used in a Simulation Chamber?
     * @param stack Data Model stack
     * @return true if Data Model can be used in a Simulation Chamber
     */
    public static boolean canSimulate(ItemStack stack) {
        return getTierData(stack)
                .map(MetadataDataModelTier::getCanSimulate)
                .orElse(false);
    }

    public static String getTierDisplayNameFormatted(ItemStack stack) {
        return getTierData(stack)
                .map(MetadataDataModelTier::getDisplayNameFormatted)
                .orElse("");
    }

    public static String getTierDisplayNameFormatted(ItemStack stack, String template) {
        return getTierData(stack)
                .map(metadata -> metadata.getDisplayNameFormatted(template))
                .orElse("");
    }

    public static String getNextTierDisplayNameFormatted(ItemStack stack) {
        return getNextTierData(stack)
                .map(MetadataDataModelTier::getDisplayNameFormatted)
                .orElse("");
    }

    public static int getTierRequiredData(ItemStack stack) {
        return getTierData(stack)
                .map(MetadataDataModelTier::getDataToNext)
                .orElse(0);
    }

    public static int getTierKillMultiplier(ItemStack stack) {
        return getTierData(stack)
                .map(MetadataDataModelTier::getKillMultiplier)
                .orElse(0);
    }

    public static int getKillsToNextTier(ItemStack stack) {
        int dataRequired = getTierRequiredData(stack);
        int dataCurrent = getCurrentTierDataCount(stack);
        int killMultiplier = getTierKillMultiplier(stack);
        return isMaxTier(stack) ? 0 :
                MathHelper.divideAndRoundUp(dataRequired - dataCurrent, killMultiplier);
    }

    public static int getSimulationEnergy(ItemStack stack) {
        return getDataModelMetadata(stack)
                .map(MetadataDataModel::getSimulationRFCost)
                .orElse(0);
    }

    public static int getPristineChance(ItemStack stack) {
        return getTierData(stack)
                .map(MetadataDataModelTier::getPristineChance)
                .orElse(0);
    }

    public static boolean getDataModelMatchesLivingMatter(ItemStack dataModel, ItemStack livingMatter) {
        return getDataModelMetadata(dataModel)
                .map(data -> data.getLivingMatter().isItemEqual(livingMatter))
                .orElse(false);
    }

    public static boolean getDataModelMatchesPristineMatter(ItemStack dataModel, ItemStack pristineMatter) {
        return getDataModelMetadata(dataModel)
                .map(data -> data.getPristineMatter().isItemEqual(pristineMatter))
                .orElse(false);
    }

    public static ImmutableList<ItemStack> getDataModelStacksFromList(List<ItemStack> stackList) {
        return stackList.stream()
                .filter(ItemStackHelper::isDataModel)
                .collect(ImmutableList.toImmutableList());
    }

    public static ItemStack getHighestTierDataModelFromList(List<ItemStack> stackList) {
        return stackList.stream()
                .max(Comparator.comparingInt(DataModelHelper::getTierLevel))
                .orElse(ItemStack.EMPTY);
    }

    //
    // Data Manipulation
    //

    public static void addSimulation(ItemStack stack) {
        increaseDataCount(stack, 1);
        setTotalSimulationCount(stack, getTotalSimulationCount(stack) + 1);
        tryIncreaseTier(stack);
    }

    private static void increaseDataCount(ItemStack stack, int amount) {
        int data = getCurrentTierDataCount(stack);
        setCurrentTierDataCount(stack, data + amount);
    }

    public static void addKill(ItemStack stack, EntityPlayerMP player) {
        getTierData(stack).ifPresent(tierData -> {
            int increase = tierData.getKillMultiplier();

            // TODO: Trial stuff

            if (ItemStackHelper.isGlitchSword(player.getHeldItemMainhand()) /* && no trial active */)
                increase *= 2;
            increaseDataCount(stack, increase);

            // Update appropriate total count
            setTotalKillCount(stack, getTotalKillCount(stack) + 1);

            if (tryIncreaseTier(stack)) {
                player.sendMessage(
                        new TextComponentTranslation(
                                "deepmoblearning.data_model.reached_tier",
                                stack.getDisplayName(),
                                getTierDisplayNameFormatted(stack)
                        )
                );
            }
        });
    }

    /**
     * Increase tier of data model if current data has reached required data
     *
     * @param stack DataModel stack to process
     * @return true if tier could be increased, otherwise false
     */
    private static boolean tryIncreaseTier(ItemStack stack) {
        if (isMaxTier(stack))
            return false;

        int currentData = getCurrentTierDataCount(stack);
        int requiredData = getTierRequiredData(stack);

        if (currentData >= requiredData) {
            setCurrentTierDataCount(stack, currentData - requiredData); // extra data carries over to higher tier
            setTierLevel(stack, getTierLevel(stack) + 1);

            return true;
        }

        return false;
    }

    //
    // Inventory Data Manipulation (e.g. Creative Model Learner)
    //
    public static void findAndLevelUpModels(NonNullList<ItemStack> inventory, EntityPlayerMP player, CreativeLevelUpAction action) {
        for (ItemStack inventoryStack : inventory) {
            if (ItemStackHelper.isDeepLearner(inventoryStack)) {
                NonNullList<ItemStack> deepLearnerContents = ItemDeepLearner.getContainedItems(inventoryStack);
                for (ItemStack modelStack : deepLearnerContents) {
                    if (ItemStackHelper.isDataModel(modelStack)) {
                        int tier = getTierLevel(modelStack);
                        switch (action) {
                            case DECREASE_TIER:
                                if (tier > 0)
                                    setTierLevel(modelStack, tier - 1);
                                break;
                            case INCREASE_TIER:
                                if (!isMaxTier(modelStack))
                                    setTierLevel(modelStack, tier + 1);
                                break;
                            case INCREASE_KILLS:
                                if (!isMaxTier(modelStack))
                                    addKill(modelStack, player);
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
