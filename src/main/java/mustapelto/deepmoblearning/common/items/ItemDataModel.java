package mustapelto.deepmoblearning.common.items;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModelTier;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModelTiers;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModels;
import mustapelto.deepmoblearning.common.util.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ItemDataModel extends ItemBase {
    public static final String NBT_METADATA_KEY = "metadataKey";

    public ItemDataModel() {
        super("data_model", 1, true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab))
            return;

        items.add(new ItemStack(this));
        for(MetadataDataModel metadata : MetadataManagerDataModels.INSTANCE.getDataStore().values()) {
            items.add(metadata.getDataModel());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        MetadataDataModel metadata = DataModelHelper.getDataModelMetadata(stack);

        if (metadata == null)
            return;

        String extraToolTip = metadata.getExtraTooltip();
        if (!extraToolTip.equals("")) {
            tooltip.add(extraToolTip);
        }

        if (!KeyboardHelper.isHoldingSneakKey()) {
            String sneakString = StringHelper.getFormattedString(TextFormatting.ITALIC, KeyboardHelper.getSneakKeyName(), TextFormatting.GRAY);
            tooltip.add(TextFormatting.GRAY + I18n.format("deepmoblearning.general.more_info", sneakString) + TextFormatting.RESET);
        } else {
            if (!DMLConfig.GENERAL_SETTINGS.SHOW_TIER_IN_NAME) {
                // Tier not shown in item name -> show in tooltip
                String displayName = DataModelHelper.getTierDisplayNameFormatted(stack);
                tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.tier", displayName) + TextFormatting.RESET);
            }

            if (!DataModelHelper.isAtMaxTier(stack)) {
                int currentData = DataModelHelper.getCurrentTierDataCount(stack);
                int requiredData = DataModelHelper.getTierRequiredData(stack);
                int currentKillMultiplier = DataModelHelper.getTierKillMultiplier(stack);
                tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.data_collected", TextFormatting.GRAY + String.valueOf(currentData), String.valueOf(requiredData) + TextFormatting.RESET));
                tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.kill_multiplier", TextFormatting.GRAY + String.valueOf(currentKillMultiplier) + TextFormatting.RESET));
            }

            int rfCost = metadata.getSimulationRFCost();
            tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.rf_cost", TextFormatting.GRAY + String.valueOf(rfCost)) + TextFormatting.RESET);

            ItemStack livingMatter = metadata.getLivingMatter();
            ItemLivingMatter livingMatterItem = (ItemLivingMatter) livingMatter.getItem();
            tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.type", livingMatterItem.getLivingMatterData().getDisplayNameFormatted()));

            boolean canSimulate = DataModelHelper.canSimulate(stack);
            if (!canSimulate) {
                tooltip.add(TextFormatting.RESET + "" + TextFormatting.RED + I18n.format("deepmoblearning.data_model.cannot_simulate") + TextFormatting.RESET);
            }
        }
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(ItemStack stack) {
        MetadataDataModel metadata = DataModelHelper.getDataModelMetadata(stack);
        String mobName;
        String tier;

        if (metadata == null) {
            mobName = DMLRelearned.proxy.getLocalizedString("deepmoblearning.data_model.blank_name");
            tier = "";
        } else {
            mobName = metadata.getDisplayName();
            tier = DMLConfig.GENERAL_SETTINGS.SHOW_TIER_IN_NAME ? DataModelHelper.getTierDisplayNameFormatted(stack, " (%s)") : "";
        }

        String name = DMLRelearned.proxy.getLocalizedString("deepmoblearning.data_model.display_name", mobName);
        return TextFormatting.AQUA + name + tier + TextFormatting.RESET;
    }

    //
    // ItemStack methods - instanceof checker
    //

    public static boolean isDataModel(ItemStack stack) {
        return stack.getItem() instanceof ItemDataModel;
    }

    //
    // ItemStack methods - NBT getters / setters
    //

    public static final String NBT_TIER = "tier";
    public static final String NBT_DATA_COUNT = "dataCount";
    public static final String NBT_TOTAL_KILL_COUNT = "totalKillCount";
    public static final String NBT_TOTAL_SIMULATION_COUNT = "totalSimulationCount";

    public static int getTierLevel(ItemStack stack) {
        return isDataModel(stack) ? NBTHelper.getInteger(stack, NBT_TIER) : 0;
    }

    public static void setTierLevel(ItemStack stack, int level) {
        NBTHelper.setInteger(stack, NBT_TIER, level);
    }

    public static int getCurrentTierDataCount(ItemStack stack) {
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
        String metadataKey = NBTHelper.getString(stack, NBT_METADATA_KEY);
        return MetadataManagerDataModels.INSTANCE.getByKey(metadataKey);
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
                .max(Comparator.comparingInt(ItemDataModel::getTierLevel))
                .orElse(ItemStack.EMPTY);
    }
}
