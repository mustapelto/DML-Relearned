package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemPristineMatter extends ItemBase {
    private final MetadataDataModel metadata;

    public ItemPristineMatter(MetadataDataModel metadata) {
        super(metadata.getPristineMatterRegistryName().getResourcePath(), 64, metadata.isModLoaded());
        this.metadata = metadata;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true; // Make items glow
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(ItemStack stack) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            return super.getItemStackDisplayName(stack); // Can't do localization on server side

        return I18n.format("deepmoblearning.pristine_matter.display_name", metadata.getDisplayName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.AQUA + I18n.format("deepmoblearning.pristine_matter.loot_items") + TextFormatting.RESET);
        for (ItemStack lootStack : metadata.getLootItems()) {
            tooltip.add(lootStack.getDisplayName());
        }
    }

    private MetadataDataModel getDataModelMetadata() {
        return metadata;
    }

    @Nullable
    public static MetadataDataModel getDataModelMetadata(ItemStack stack) {
        if (!ItemStackHelper.isPristineMatter(stack))
            return null;

        return ((ItemPristineMatter) stack.getItem()).getDataModelMetadata();
    }
}
