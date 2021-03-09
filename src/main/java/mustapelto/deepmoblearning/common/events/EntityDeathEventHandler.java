package mustapelto.deepmoblearning.common.events;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.common.items.*;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@EventBusSubscriber
public class EntityDeathEventHandler {
    //TODO: Add Trial Stuff
    private static final Integer entityUUIDBlacklistCap = 1000;
    private static final NonNullList<UUID> killedEntityUUIDBlacklist = NonNullList.create();

    @SubscribeEvent
    public static void dropEvent(LivingDropsEvent event) {

    }

    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer)
            handlePlayerDeath(event);
        else
            handleMobDeath(event);
    }

    private static void handlePlayerDeath(LivingDeathEvent event) {
        // TODO: implement
    }

    private static void handleMobDeath(LivingDeathEvent event) {
        Entity source = event.getSource().getTrueSource();
        EntityLivingBase target = event.getEntityLiving();

        // If blacklist is at cap -> clear list
        if (killedEntityUUIDBlacklist.size() >= entityUUIDBlacklistCap)
            cullEntityUUIDBlacklist();

        if (isEntityUUIDBlacklisted(target))
            return;

        // TODO: Trial stuff

        if (source instanceof EntityPlayer)
            handlePlayerKill((EntityPlayerMP) source, target);

        killedEntityUUIDBlacklist.add(target.getUniqueID());
    }

    private static void handlePlayerKill(EntityPlayerMP player, EntityLivingBase target) {
        NonNullList<ItemStack> inventory = NonNullList.create();
        inventory.addAll(player.inventory.mainInventory);
        inventory.addAll(player.inventory.offHandInventory);

        // Find deep learners and trial keys from inventory
        ImmutableList<ItemStack> deepLearners = inventory.stream()
                .filter(stack -> stack.getItem() instanceof ItemDeepLearner)
                .collect(ImmutableList.toImmutableList());

        ImmutableList<ItemStack> trialKeys = inventory.stream()
                .filter(key -> key.getItem() instanceof ItemTrialKey && !ItemTrialKey.isAttuned(key))
                .collect(ImmutableList.toImmutableList());

        ImmutableList<ItemStack> updatedModels = updateModels(deepLearners, player, target);

        if (updatedModels.isEmpty())
            return; // No models found -> nothing more to do

        ItemStack highestTierModel = DataModelHelper.getHighestTierDataModelFromList(updatedModels);

        // Chance to drop pristine matter from the model that gained data
        if (ItemGlitchArmor.isSetEquipped(player)) {
            // TODO: Don't run if player in trial
            ItemGlitchArmor.dropPristineMatter(target.world, target.getPosition(), highestTierModel);
        }

        if (player.getHeldItemMainhand().getItem() instanceof ItemGlitchSword) {
            // TODO: Don't run if player in trial
            ItemStack sword = player.getHeldItemMainhand();
            if (ItemGlitchSword.canIncreaseDamage(sword)) {
                ItemGlitchSword.increaseDamage(sword, player);
            }
        }

        // Attune Trial Keys to updated Model
        trialKeys.forEach(key -> tryAttuneTrialKey(key, highestTierModel, player));
    }

    //
    // Helper Functions
    //

    /** Update all Data Models of the appropriate type
     *
     * @param deepLearners List of Deep Learners in player's inventory
     * @param player Player who made the kill
     * @param target Entity that was killed
     * @return List of updated Data Models
     */
    private static ImmutableList<ItemStack> updateModels(ImmutableList<ItemStack> deepLearners, EntityPlayerMP player, EntityLivingBase target) {
        ImmutableList.Builder<ItemStack> updatedModelsBuilder = ImmutableList.builder();

        deepLearners.forEach(deepLearner -> {
           NonNullList<ItemStack> containedItems = ItemDeepLearner.getContainedItems(deepLearner);

           containedItems.forEach(stack -> {
               if (!ItemDataModel.isDataModel(stack))
                   return;

               MetadataDataModel dataModelMetadata = DataModelHelper.getDataModelMetadata(stack);
               if (dataModelMetadata == null)
                   return;

               if (dataModelMetadata.isAssociatedMob(target)) {
                   DataModelHelper.addKill(stack, player);
                   updatedModelsBuilder.add(stack);
               }
           });

            ItemDeepLearner.setContainedItems(deepLearner, containedItems);
        });

        return updatedModelsBuilder.build();
    }

    // UUID Blacklist Functions

    private static void cullEntityUUIDBlacklist() {
        UUID lastUUID = killedEntityUUIDBlacklist.get(killedEntityUUIDBlacklist.size() - 1);
        killedEntityUUIDBlacklist.clear();
        killedEntityUUIDBlacklist.add(lastUUID);
    }

    private static boolean isEntityUUIDBlacklisted(EntityLivingBase entityLiving) {
        return killedEntityUUIDBlacklist.stream()
                .anyMatch(uuid -> uuid.compareTo(entityLiving.getUniqueID()) == 0);
    }

    private static void tryAttuneTrialKey(ItemStack trialKey, ItemStack dataModel, EntityPlayerMP player) {
        // Don't have to test for isTrialKey - isAttuned takes care of that
        if (ItemTrialKey.isAttuned(trialKey) || dataModel.isEmpty() || !ItemDataModel.isDataModel(dataModel))
            return;

        MetadataDataModel metadataDataModel = DataModelHelper.getDataModelMetadata(dataModel);
        if (metadataDataModel == null)
            return;

        MetadataDataModel.TrialData trialData = metadataDataModel.getTrialData();
        if (trialData.hasEntity()) {
            ItemTrialKey.attune(trialKey, dataModel, player);
            // TODO: addAffixes
        } else {
            player.sendMessage(new TextComponentTranslation("deepmoblearning.trial_key.cannot_attune_message", trialKey.getDisplayName(), metadataDataModel.getDisplayName()));
        }
    }
}
