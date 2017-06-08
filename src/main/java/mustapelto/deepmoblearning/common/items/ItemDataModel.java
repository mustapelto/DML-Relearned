package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumMobType;
import mustapelto.deepmoblearning.common.util.DataModel;
import mustapelto.deepmoblearning.common.util.KeyboardHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDataModel extends ItemBase {
    private final EnumMobType mobType;

    public ItemDataModel(EnumMobType mobType) {
        super("data_model_" + mobType.getName(), 1);
        this.mobType = mobType;
    }

    public EnumMobType getMobType() {
        return mobType;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String extraToolTip = DataModel.getExtraTooltip(stack);
        if (!extraToolTip.equals("")) {
            tooltip.add(extraToolTip);
        }

        if (!KeyboardHelper.isHoldingSneakKey()) {
            String sneakKey = KeyboardHelper.getSneakDisplayName();
            tooltip.add(I18n.format("deepmoblearning.data_model.more_info", sneakKey));
        } else {
            String displayName = DataModel.getTierDisplayNameFormatted(stack);
            tooltip.add(I18n.format("deepmoblearning.data_model.tier", displayName));

            int tier = DataModel.getTierLevel(stack);
            if (tier < DMLConstants.DataModel.MAX_TIER) {
                int currentData = DataModel.getCurrentTierCurrentData(stack);
                int requiredData = DataModel.getCurrentTierRequiredData(stack);
                int currentKillMultiplier = DataModel.getCurrentKillMultiplier(stack);
                tooltip.add(I18n.format("deepmoblearning.data_model.data_collected", currentData, requiredData));
                tooltip.add(I18n.format("deepmoblearning.data_model.kill_multiplier", currentKillMultiplier));
            }

            int rfCost = DataModel.getCurrentTierSimulationCost(stack);
            String livingMatterName = DataModel.getLivingMatterDisplayNameFormatted(stack);
            tooltip.add(I18n.format("deepmoblearning.data_model.rf_cost", rfCost));
            tooltip.add(I18n.format("deepmoblearning.data_model.type", livingMatterName));
        }
    }
}
