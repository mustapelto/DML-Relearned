package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.client.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import mustapelto.deepmoblearning.common.util.StringHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemTrialKey extends ItemBase {
    public ItemTrialKey() {
        super("trial_key", 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (!KeyboardHelper.isHoldingSneakKey()) {
            String sneakString = StringHelper.getFormattedString(TextFormatting.ITALIC, KeyboardHelper.getSneakKeyName(), TextFormatting.GRAY);
            tooltip.add(TextFormatting.GRAY + I18n.format("deepmoblearning.general.more_info", sneakString) + TextFormatting.RESET);
        } else {
            String attunement = getAttunement(stack);
            if (attunement.isEmpty()) {
                tooltip.add(StringHelper.getFormattedString(I18n.format("deepmoblearning.trial_key.tooltip.not_attuned"), TextFormatting.GRAY));
                tooltip.add(StringHelper.getFormattedString(I18n.format("deepmoblearning.trial_key.tooltip.available_attunements"), TextFormatting.AQUA));
            } else {

            }
        }
    }

    //
    // ItemStack methods
    //

    public static void attune(@Nonnull ItemStack trialKey, @Nonnull ItemStack dataModel, @Nonnull EntityPlayerMP player) {
        if (!(trialKey.getItem() instanceof ItemTrialKey) || !(dataModel.getItem() instanceof ItemDataModel))
            return;

        MetadataDataModel metadataDataModel = DataModelHelper.getDataModelMetadata(dataModel);
        NBTHelper.setString(trialKey, ATTUNEMENT, metadataDataModel.getMetadataID());
        NBTHelper.setInteger(trialKey, TIER, DataModelHelper.getTierLevel(dataModel));

        player.sendMessage(new TextComponentTranslation("deepmoblearning.trial_key.attunement_message", trialKey.getDisplayName(), dataModel.getDisplayName()));
    }

    private static String getAttunement(@Nonnull ItemStack trialKey) {
        if (!(trialKey.getItem() instanceof ItemTrialKey))
            return "";

        if (!NBTHelper.hasKey(trialKey, DML_RELEARNED))
            return NBTHelper.getString(trialKey, OLD_MOB_KEY, "");
        else
            return NBTHelper.getString(trialKey, ATTUNEMENT, "");
    }

    private static final String OLD_ATTUNED = "attuned";
    private static final String OLD_MOB_KEY = "mobKey";

    private static final String DML_RELEARNED = "dml-relearned";
    private static final String ATTUNEMENT = "attunement";
    private static final String TIER = "tier";
}
