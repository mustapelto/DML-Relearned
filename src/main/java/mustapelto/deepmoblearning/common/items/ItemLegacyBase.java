package mustapelto.deepmoblearning.common.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemLegacyBase extends ItemBase {
    public ItemLegacyBase(String name, int stackSize) {
        super(name, stackSize, false);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            ItemStack old = playerIn.getHeldItem(handIn);
            ItemStack converted = getConvertedItemStack(old);
            int oldCount = old.getCount();
            converted.setCount(oldCount);
            old.shrink(oldCount);

            playerIn.inventory.addItemStackToInventory(converted);
        }

        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("deepmoblearning.legacy.tooltip_1"));
        tooltip.add(I18n.format("deepmoblearning.legacy.tooltip_2"));
        tooltip.add(I18n.format("deepmoblearning.legacy.tooltip_3"));
        tooltip.add(I18n.format("deepmoblearning.legacy.tooltip_4"));
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.RED + "(LEGACY)" + TextFormatting.RESET;
    }

    public abstract ItemStack getConvertedItemStack(ItemStack old);
}
