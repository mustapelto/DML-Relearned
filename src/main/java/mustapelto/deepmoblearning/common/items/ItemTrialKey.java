package mustapelto.deepmoblearning.common.items;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.client.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModelTier;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModelTiers;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModels;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import mustapelto.deepmoblearning.common.util.StringHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!KeyboardHelper.isHoldingSneakKey()) {
            String sneakString = StringHelper.getFormattedString(TextFormatting.ITALIC, KeyboardHelper.getSneakKeyName(), TextFormatting.GRAY);
            tooltip.add(TextFormatting.GRAY + I18n.format("deepmoblearning.general.more_info", sneakString) + TextFormatting.RESET);
        } else {
            String attunement = getAttunement(stack);
            if (attunement.isEmpty()) {
                tooltip.add(StringHelper.getFormattedString(I18n.format("deepmoblearning.trial_key.tooltip.not_attuned"), TextFormatting.GRAY));
                tooltip.add(StringHelper.getFormattedString(I18n.format("deepmoblearning.trial_key.tooltip.available_attunements"), TextFormatting.AQUA));

                ImmutableList<String> availableTrials = MetadataManagerDataModels.INSTANCE.getAvailableTrials();
                for (String trial : availableTrials)
                    tooltip.add(StringHelper.getFormattedString("  - " + trial, TextFormatting.WHITE));
            } else {
                MetadataDataModel attunementMetadata = getAttunementMetadata(stack);
                MetadataDataModelTier attunementTierMetadata = getAttunementTier(stack);
                if (attunementMetadata == null || attunementTierMetadata.isInvalid())
                    return;

                String mobName = StringHelper.getFormattedString(attunementMetadata.getDisplayName(), TextFormatting.GRAY);
                tooltip.add(I18n.format("deepmoblearning.trial_key.tooltip.attunement", mobName));

                String tierName = attunementTierMetadata.getDisplayNameFormatted();
                tooltip.add(I18n.format("deepmoblearning.trial_key.tooltip.tier",tierName));

                // TODO: Affixes
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return super.initCapabilities(stack, nbt);
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(ItemStack stack) {
        return StringHelper.getFormattedString(super.getItemStackDisplayName(stack), TextFormatting.AQUA);
    }

    //
    // ItemStack methods
    //

    public static void attune(ItemStack trialKey, ItemStack dataModel, EntityPlayerMP player) {
        MetadataDataModel metadataDataModel = DataModelHelper.getDataModelMetadata(dataModel);
        NBTHelper.setVersion(trialKey);
        NBTHelper.setString(trialKey, ATTUNEMENT, metadataDataModel.getMetadataID());
        NBTHelper.setInteger(trialKey, TIER, DataModelHelper.getTierLevel(dataModel));

        player.sendMessage(new TextComponentTranslation("deepmoblearning.trial_key.attunement_message", trialKey.getDisplayName(), metadataDataModel.getDisplayName()));
    }

    public static boolean isAttuned(ItemStack trialKey) {
        return !getAttunement(trialKey).isEmpty();
    }

    private static String getAttunement(ItemStack trialKey) {
        if (!ItemStackHelper.isTrialKey(trialKey))
            return "";

        if (NBTHelper.isLegacyNBT(trialKey))
            return NBTHelper.getString(trialKey, OLD_MOB_KEY, "");
        else
            return NBTHelper.getString(trialKey, ATTUNEMENT, "");
    }

    @Nullable
    private static MetadataDataModel getAttunementMetadata(ItemStack trialKey) {
        if (!ItemStackHelper.isTrialKey(trialKey))
            return null;

        String dataModelID = getAttunement(trialKey);

        return MetadataManagerDataModels.INSTANCE.getByKey(dataModelID);
    }

    private static MetadataDataModelTier getAttunementTier(ItemStack trialKey) {
        if (!ItemStackHelper.isTrialKey(trialKey))
            return MetadataDataModelTier.INVALID;

        int tierLevel = NBTHelper.getInteger(trialKey, TIER, -1);

        return MetadataManagerDataModelTiers.INSTANCE.getByLevel(tierLevel);
    }

    private static final String OLD_MOB_KEY = "mobKey";

    private static final String ATTUNEMENT = "attunement";
    private static final String TIER = "tier";
}
