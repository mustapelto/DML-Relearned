package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.DMLRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemGlitchIngot extends ItemBase {
    public ItemGlitchIngot() {
        super("glitch_infused_ingot", 64);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        String glitchFragment = new ItemStack(DMLRegistry.itemGlitchFragment).getDisplayName();
        tooltip.add(I18n.format("deepmoblearning.glitch_ingot.tooltip_1", glitchFragment));
        tooltip.add(I18n.format("deepmoblearning.glitch_ingot.tooltip_2"));
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return TextFormatting.AQUA + super.getItemStackDisplayName(stack) + TextFormatting.RESET;
    }
}
