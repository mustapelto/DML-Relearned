package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
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
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLivingMatter extends ItemBase {
    private final EnumLivingMatterType type;

    public ItemLivingMatter(EnumLivingMatterType type) {
        super("living_matter_" + type.getName(), 64);
        this.type = type;
    }

    public EnumLivingMatterType getType() {
        return type;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("deepmoblearning.living_matter.consume_for_xp", KeyboardHelper.getUseDisplayName()));
        tooltip.add(I18n.format("deepmoblearning.living_matter.consume_stack", KeyboardHelper.getSneakDisplayName()));
        tooltip.add(I18n.format("deepmoblearning.living_matter.xp", DMLConfig.LIVING_MATTER.getLivingMatterXP(type)));
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
}
