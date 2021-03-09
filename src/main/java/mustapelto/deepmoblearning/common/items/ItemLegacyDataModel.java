package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.DMLRegistry;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModelTier;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModelTiers;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class ItemLegacyDataModel extends ItemLegacyBase {
    private static final String NBT_LEGACY_SIMULATION_COUNT = "simulationCount";
    private static final String NBT_LEGACY_KILL_COUNT = "killCount";

    private final String mobName;

    ItemLegacyDataModel(LegacyMobTypes mobType) {
        super("data_model_" + mobType.name, 1);
        this.mobName = mobType.name;
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.format("deepmoblearning.legacy.data_model", super.getItemStackDisplayName(stack));
    }

    public static boolean isLegacyDataModel(ItemStack stack) {
        return stack.getItem() instanceof ItemLegacyDataModel;
    }

    @Override
    public ItemStack getConvertedItemStack(ItemStack old) {
        ItemStack converted = new ItemStack(DMLRegistry.ITEM_DATA_MODEL);

        // Set mob name
        NBTHelper.setString(converted, ItemDataModel.NBT_METADATA_KEY, mobName);

        // Copy entries with identical key
        int totalSimulationCount = NBTHelper.getInteger(old, ItemDataModel.NBT_TOTAL_SIMULATION_COUNT);
        NBTHelper.setInteger(converted, ItemDataModel.NBT_TOTAL_SIMULATION_COUNT, totalSimulationCount);

        int totalKillCount = NBTHelper.getInteger(old, ItemDataModel.NBT_TOTAL_KILL_COUNT);
        NBTHelper.setInteger(converted, ItemDataModel.NBT_TOTAL_KILL_COUNT, totalKillCount);

        int tier = NBTHelper.getInteger(old, ItemDataModel.NBT_TIER);
        NBTHelper.setInteger(converted, ItemDataModel.NBT_TIER, tier);

        // Calculate total data from simulations and kills
        Optional<MetadataDataModelTier> tierData = MetadataManagerDataModelTiers.INSTANCE.getByLevel(tier);
        if (!tierData.isPresent()) {
            NBTHelper.setInteger(converted, ItemDataModel.NBT_DATA_COUNT, 0);
        } else {
            int currentSimulations = NBTHelper.getInteger(old, NBT_LEGACY_SIMULATION_COUNT);
            int currentKills = NBTHelper.getInteger(old, NBT_LEGACY_KILL_COUNT);
            int dataCount = currentSimulations + currentKills * tierData.get().getKillMultiplier();

            NBTHelper.setInteger(converted, ItemDataModel.NBT_DATA_COUNT, dataCount);
        }

        return converted;
    }

    public enum LegacyMobTypes {
        BLAZE("blaze");

        private final String name;

        LegacyMobTypes(String name) {
            this.name = name;
        }
    }
}
