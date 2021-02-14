package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.MetadataLivingMatter;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageLivingMatterConsume;
import mustapelto.deepmoblearning.common.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.util.StringHelper;
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

public class ItemLivingMatter extends ItemBase {
    private final MetadataLivingMatter metadata;

    public ItemLivingMatter(MetadataLivingMatter metadata) {
        super(metadata.getLivingMatterRegistryName().getResourcePath(), 64, metadata.isModLoaded());
        this.metadata = metadata;
    }

    public MetadataLivingMatter getLivingMatterData() {
        return metadata;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        String useString = StringHelper.getFormattedString(TextFormatting.ITALIC, Minecraft.getMinecraft().gameSettings.keyBindUseItem.getDisplayName(), TextFormatting.GRAY);
        String sneakString = StringHelper.getFormattedString(TextFormatting.ITALIC, Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName(), TextFormatting.GRAY);
        tooltip.add(I18n.format("deepmoblearning.living_matter.consume_for_xp", useString));
        tooltip.add(I18n.format("deepmoblearning.living_matter.consume_stack", sneakString));
        tooltip.add(I18n.format("deepmoblearning.living_matter.xp", metadata.getXpValue()));
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
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
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return metadata.getDisplayNameFormatted() + " Matter";
    }
}
