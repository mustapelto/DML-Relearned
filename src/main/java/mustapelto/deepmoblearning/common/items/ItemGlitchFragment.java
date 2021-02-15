package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.DMLRegistry;
import mustapelto.deepmoblearning.common.entities.EntityItemGlitchFragment;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemGlitchFragment extends ItemBase {
    public ItemGlitchFragment() {
        super("glitch_fragment", 64);
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return TextFormatting.AQUA + super.getItemStackDisplayName(stack) + TextFormatting.RESET;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        String glitchHeart = new ItemStack(DMLRegistry.ITEM_GLITCH_HEART).getDisplayName();
        String obsidian = TextFormatting.RESET + I18n.format("tile.obsidian.name") + TextFormatting.GRAY;
        String leftClick = KeyboardHelper.getAttackKeyName();
        tooltip.add(I18n.format("deepmoblearning.glitch_fragment.tooltip_1", glitchHeart));
        tooltip.add(I18n.format("deepmoblearning.glitch_fragment.tooltip_2", obsidian, leftClick));
        tooltip.add(I18n.format("deepmoblearning.glitch_fragment.tooltip_3", DMLConstants.Crafting.GLITCH_FRAGMENTS_PER_HEART));
    }

    @Override
    public boolean hasCustomEntity(@Nonnull ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(@Nonnull World world, @Nonnull Entity location, @Nonnull ItemStack itemstack) {
        Entity result = new EntityItemGlitchFragment(world, location.posX, location.posY, location.posZ, new ItemStack(this, itemstack.getCount()));
        result.motionX = location.motionX;
        result.motionY = location.motionY;
        result.motionZ = location.motionZ;
        return result;
    }
}
