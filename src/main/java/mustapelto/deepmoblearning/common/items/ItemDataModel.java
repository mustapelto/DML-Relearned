package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.client.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.StringHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemDataModel extends ItemBase {
    private final MetadataDataModel metadata;

    public ItemDataModel(MetadataDataModel metadata) {
        super(metadata.getDataModelRegistryName().getResourcePath(), 1, metadata.isModLoaded());
        this.metadata = metadata;
    }

    public MetadataDataModel getDataModelMetadata() {
        return metadata;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        MetadataDataModel metadata = DataModelHelper.getDataModelMetadata(stack);

        if (metadata.isInvalid())
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
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            return super.getItemStackDisplayName(stack); // Can't do localization on server side

        String name = I18n.format("deepmoblearning.data_model.display_name", metadata.getDisplayName());
        String tier = DMLConfig.GENERAL_SETTINGS.SHOW_TIER_IN_NAME ? DataModelHelper.getTierDisplayNameFormatted(stack, " (%s)") : "";
        return TextFormatting.AQUA + name + tier + TextFormatting.RESET;
    }
}
