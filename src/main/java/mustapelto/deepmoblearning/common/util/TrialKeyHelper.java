package mustapelto.deepmoblearning.common.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mustapelto.deepmoblearning.common.trials.AttunementData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Optional;

public class TrialKeyHelper {
    private static final String NBT_ATTUNEMENT = "attunement";
    private static final String NBT_TIER = "tier";
    private static final String NBT_LEGACY_MOB_KEY = "mobKey";

    private static final Table<String, Integer, AttunementData> attunementDataCache = HashBasedTable.create();

    public static void attune(ItemStack trialKey, ItemStack dataModel, EntityPlayerMP player) {
        DataModelHelper.getDataModelMetadata(dataModel)
                .ifPresent(metadata -> {
                    NBTHelper.setString(trialKey, NBT_ATTUNEMENT, metadata.getID());
                    NBTHelper.setInteger(trialKey, NBT_TIER, DataModelHelper.getTier(dataModel));

                    player.sendMessage(new TextComponentTranslation("deepmoblearning.trial_key.attunement_message", trialKey.getDisplayName(), metadata.getDisplayName()));
                });
    }

    public static boolean isAttuned(ItemStack trialKey) {
        return getAttunement(trialKey).isPresent();
    }

    private static Optional<String> getAttunementString(ItemStack trialKey) {
        if (!ItemStackHelper.isTrialKey(trialKey))
            return Optional.empty();

        if (NBTHelper.hasKey(trialKey, NBT_LEGACY_MOB_KEY))
            convertNBT(trialKey);

        String attunement = NBTHelper.getString(trialKey, NBT_ATTUNEMENT);
        return !attunement.isEmpty() ? Optional.of(attunement) : Optional.empty();
    }

    public static Optional<AttunementData> getAttunement(ItemStack trialKey) {
        String mob = getAttunementString(trialKey).orElse("");
        int tier = NBTHelper.getInteger(trialKey, NBT_TIER, -1);

        if (!attunementDataCache.contains(mob, tier))
            AttunementData.create(mob, tier)
                    .ifPresent(data -> attunementDataCache.put(mob, tier, data));

        AttunementData result = attunementDataCache.get(mob, tier);
        return (result != null) ? Optional.of(result) : Optional.empty();
    }

    private static void convertNBT(ItemStack stack) {
        String mobKey = NBTHelper.getString(stack, NBT_LEGACY_MOB_KEY);
        NBTHelper.setString(stack,NBT_ATTUNEMENT, mobKey);
        NBTHelper.removeKey(stack, NBT_LEGACY_MOB_KEY);
    }
}
