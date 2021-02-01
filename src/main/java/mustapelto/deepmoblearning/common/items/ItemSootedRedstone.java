package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.DMLConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSootedRedstone extends ItemBase {
    public ItemSootedRedstone() {
        super("soot_covered_redstone", 64);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (DMLConfig.GENERAL_SETTINGS.SOOT_COVERED_REDSTONE_CRAFTING_ENABLED) {
            String redstone = TextFormatting.RED + I18n.format("item.redstone.name") + TextFormatting.GRAY;
            String coal = TextFormatting.RESET + I18n.format("tile.blockCoal.name") + TextFormatting.GRAY;
            String leftClick = Minecraft.getMinecraft().gameSettings.keyBindAttack.getDisplayName();
            tooltip.add(I18n.format("deepmoblearning.soot_covered_redstone.tooltip_1", redstone));
            tooltip.add(I18n.format("deepmoblearning.soot_covered_redstone.tooltip_2", coal, leftClick));
        }
    }
}
