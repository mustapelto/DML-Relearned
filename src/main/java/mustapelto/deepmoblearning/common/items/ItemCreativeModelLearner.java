package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageLevelUpModel;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.DataModelHelper.CreativeLevelUpAction;
import mustapelto.deepmoblearning.common.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.util.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemCreativeModelLearner extends DMLItem {
    public ItemCreativeModelLearner() {
        super("creative_model_learner", 1);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        if (playerIn.world.isRemote) {
            CreativeLevelUpAction action;
            if (KeyboardHelper.isHoldingSneakKey())
                action = CreativeLevelUpAction.INCREASE_TIER;
            else if (KeyboardHelper.isHoldingSprintKey())
                action = CreativeLevelUpAction.DECREASE_TIER;
            else
                action = CreativeLevelUpAction.INCREASE_KILLS;

            DMLPacketHandler.network.sendToServer(new MessageLevelUpModel(action));
        }

        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (!KeyboardHelper.isHoldingSneakKey()) {
            String sneakString = TextFormatting.RESET + "" + TextFormatting.ITALIC + Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName() + TextFormatting.RESET + "" + TextFormatting.GRAY;
            tooltip.add(TextFormatting.GRAY + I18n.format("deepmoblearning.general.more_info", sneakString) + TextFormatting.RESET);
        } else {
            String sneakName = Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName();
            String sprintName = Minecraft.getMinecraft().gameSettings.keyBindSprint.getDisplayName();
            String useName = Minecraft.getMinecraft().gameSettings.keyBindUseItem.getDisplayName();

            String increaseTier = TextHelper.getFormattedString(TextFormatting.ITALIC, sneakName + " + " + useName, TextFormatting.GRAY);
            String decreaseTier = TextHelper.getFormattedString(TextFormatting.ITALIC, sprintName + " + " + useName, TextFormatting.GRAY);
            String increaseKills = TextHelper.getFormattedString(TextFormatting.ITALIC, useName, TextFormatting.GRAY);
            tooltip.add(I18n.format("deepmoblearning.creative_model_learner.increase_tier", increaseTier));
            tooltip.add(I18n.format("deepmoblearning.creative_model_learner.increase_tier", decreaseTier));
            tooltip.add(I18n.format("deepmoblearning.creative_model_learner.increase_tier", increaseKills));
        }
    }
}
