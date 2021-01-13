package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.KeyboardHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class ItemDataModel extends DMLItem {
    private final MobMetaData metaData;

    public ItemDataModel(MobMetaData metaData) {
        super("data_model_" + metaData.getItemID(), 1);
        this.metaData = metaData;
    }

    public MobMetaData getMobMetaData() {
        return metaData;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        MobMetaData mobMetaData = ((ItemDataModel)stack.getItem()).getMobMetaData();

        if (mobMetaData == null)
            return;

        String extraToolTip = mobMetaData.getExtraTooltip();
        if (!extraToolTip.equals("")) {
            tooltip.add(extraToolTip);
        }

        if (!KeyboardHelper.isHoldingSneakKey()) {
            String sneakKey = KeyboardHelper.getSneakDisplayName();
            tooltip.add(I18n.format("deepmoblearning.general.more_info", sneakKey));
        } else {
            String displayName = DataModelHelper.getTierDisplayNameFormatted(stack);
            tooltip.add(I18n.format("deepmoblearning.data_model.tier", displayName));

            int tier = DataModelHelper.getTierLevel(stack);
            if (tier < DMLConstants.DataModel.MAX_TIER) {
                int currentData = DataModelHelper.getCurrentTierCurrentData(stack);
                int requiredData = DataModelHelper.getCurrentTierRequiredData(stack);
                int currentKillMultiplier = DataModelHelper.getCurrentTierKillMultiplier(stack);
                tooltip.add(I18n.format("deepmoblearning.data_model.data_collected", currentData, requiredData));
                tooltip.add(I18n.format("deepmoblearning.data_model.kill_multiplier", currentKillMultiplier));
            }

            int rfCost = mobMetaData.getSimulationRFCost();

            String livingMatterName = mobMetaData.getLivingMatter();
            tooltip.add(I18n.format("deepmoblearning.data_model.rf_cost", rfCost));
            tooltip.add(I18n.format("deepmoblearning.data_model.type", livingMatterName));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return metaData.getDisplayName() + " Data Model";
    }

    public ItemMeshDefinition getMeshDefinition() {
        return new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                ResourceLocation location = stack.getItem().getRegistryName();
                try {
                    Minecraft.getMinecraft().getResourceManager().getAllResources(location);
                } catch (IOException exception) {
                    location = new ResourceLocation(DMLConstants.DataModel.DEFAULT_MODEL_NAME);
                }
                return new ModelResourceLocation(location, "inventory");
            }
        };
    }
}
