package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.DataModelTierData;
import mustapelto.deepmoblearning.common.metadata.LivingMatterData;
import mustapelto.deepmoblearning.common.metadata.MobMetaData;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.util.TextHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemDataModel extends DMLItem {
    private final MobMetaData metaData;

    public ItemDataModel(MobMetaData metaData) {
        super("data_model_" + metaData.getItemID(), 1, metaData.isModLoaded());
        this.metaData = metaData;
    }

    public MobMetaData getMobMetaData() {
        return metaData;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        MobMetaData mobMetaData = DataModelHelper.getMobMetaData(stack);

        if (mobMetaData == null)
            return;

        String extraToolTip = mobMetaData.getExtraTooltip();
        if (!extraToolTip.equals("")) {
            tooltip.add(extraToolTip);
        }

        if (!KeyboardHelper.isHoldingSneakKey()) {
            String sneakString = TextHelper.getFormattedString(TextFormatting.ITALIC, KeyboardHelper.getSneakDisplayName(), TextFormatting.GRAY);
            tooltip.add(TextFormatting.GRAY + I18n.format("deepmoblearning.general.more_info", sneakString) + TextFormatting.RESET);
        } else {
            String displayName = DataModelHelper.getTierDisplayNameFormatted(stack);
            tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.tier", displayName) + TextFormatting.RESET);

            if (!DataModelHelper.isAtMaxTier(stack)) {
                int currentData = DataModelHelper.getCurrentTierDataCount(stack);
                int requiredData = DataModelHelper.getTierRequiredData(stack);
                int currentKillMultiplier = DataModelHelper.getTierKillMultiplier(stack);
                tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.data_collected", TextFormatting.GRAY + String.valueOf(currentData), String.valueOf(requiredData) + TextFormatting.RESET));
                tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.kill_multiplier", TextFormatting.GRAY + String.valueOf(currentKillMultiplier) + TextFormatting.RESET));
            }

            int rfCost = mobMetaData.getSimulationRFCost();
            tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.rf_cost", TextFormatting.GRAY + String.valueOf(rfCost)) + TextFormatting.RESET);

            LivingMatterData livingMatterData = mobMetaData.getLivingMatterData();
            tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.data_model.type", livingMatterData.getDisplayNameFormatted()));

            boolean canSimulate = DataModelHelper.canSimulate(stack);
            if (!canSimulate) {
                tooltip.add(TextFormatting.RESET + "" + TextFormatting.RED + I18n.format("deepmoblearning.data_model.cannot_simulate") + TextFormatting.RESET);
            }
        }
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return TextFormatting.AQUA +
                I18n.format("deepmoblearning.data_model.display_name", metaData.getDisplayName()) +
                DataModelHelper.getTierDisplayNameFormatted(stack, " (%s)") +
                TextFormatting.RESET;
    }
}
