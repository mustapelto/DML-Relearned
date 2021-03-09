package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModelTier;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModelTiers;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModels;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Optional;

public class TrialKeyHelper {
    private static final String NBT_ATTUNEMENT = "attunement";
    private static final String NBT_TIER = "tier";
    private static final String NBT_LEGACY_MOB_KEY = "mobKey";

    public static void attune(ItemStack trialKey, ItemStack dataModel, EntityPlayerMP player) {
        DataModelHelper.getDataModelMetadata(dataModel)
                .ifPresent(metadata -> {
                    NBTHelper.setString(trialKey, NBT_ATTUNEMENT, metadata.getMetadataID());
                    NBTHelper.setInteger(trialKey, NBT_TIER, DataModelHelper.getTierLevel(dataModel));

                    player.sendMessage(new TextComponentTranslation("deepmoblearning.trial_key.attunement_message", trialKey.getDisplayName(), metadata.getDisplayName()));
                });
    }

    public static boolean isAttuned(ItemStack trialKey) {
        return !getAttunement(trialKey).isEmpty();
    }

    public static String getAttunement(ItemStack trialKey) {
        if (!ItemStackHelper.isTrialKey(trialKey))
            return "";

        if (NBTHelper.hasKey(trialKey, NBT_LEGACY_MOB_KEY))
            convertNBT(trialKey);

        return NBTHelper.getString(trialKey, NBT_ATTUNEMENT, "");
    }

    public static Optional<MetadataDataModel> getAttunementMetadata(ItemStack trialKey) {
        if (!ItemStackHelper.isTrialKey(trialKey))
            return Optional.empty();

        String dataModelID = getAttunement(trialKey);

        return MetadataManagerDataModels.INSTANCE.getByKey(dataModelID);
    }

    public static Optional<MetadataDataModelTier> getAttunementTier(ItemStack trialKey) {
        if (!ItemStackHelper.isTrialKey(trialKey))
            return Optional.empty();

        int tierLevel = NBTHelper.getInteger(trialKey, NBT_TIER, -1);

        return MetadataManagerDataModelTiers.INSTANCE.getByLevel(tierLevel);
    }

    private static void convertNBT(ItemStack stack) {
        String mobKey = NBTHelper.getString(stack, NBT_LEGACY_MOB_KEY);
        NBTHelper.setString(stack,NBT_ATTUNEMENT, mobKey);
        NBTHelper.removeKey(stack, NBT_LEGACY_MOB_KEY);
    }
}
