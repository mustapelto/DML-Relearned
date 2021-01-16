package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.metadata.LivingMatterData;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageLivingMatterConsume;
import mustapelto.deepmoblearning.common.util.KeyboardHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLivingMatter extends DMLItem {
    private final LivingMatterData data;

    public ItemLivingMatter(LivingMatterData data) {
        super("living_matter_" + data.getItemID(), 64, data.isModLoaded());
        this.data = data;
    }

    public LivingMatterData getData() {
        return data;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("deepmoblearning.living_matter.consume_for_xp", KeyboardHelper.getUseDisplayName()));
        tooltip.add(I18n.format("deepmoblearning.living_matter.consume_stack", KeyboardHelper.getSneakDisplayName()));
        tooltip.add(I18n.format("deepmoblearning.living_matter.xp", data.getXpValue()));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (worldIn.isRemote) {
            if (KeyboardHelper.isHoldingSneakKey()) {
                DMLPacketHandler.network.sendToServer(new MessageLivingMatterConsume(true));
            } else {
                DMLPacketHandler.network.sendToServer(new MessageLivingMatterConsume(false));
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return data.getDisplayName() + " Matter";
    }

    @Override
    public ResourceLocation getDefaultResourceLocation() {
        return new ResourceLocation(DMLConstants.LivingMatter.DEFAULT_MODEL_NAME);
    }
}
