package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return true; // Make items glow
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            return super.getItemStackDisplayName(stack); // Can't do localization on server side

        return I18n.format("deepmoblearning.pristine_matter.display_name", metadata.getDisplayName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        // TODO: show loot items in tooltip
    }

    private MetadataDataModel getDataModelMetadata() {
        return metadata;
    }

    public static MetadataDataModel getDataModelMetadata(ItemStack stack) {
        Item stackItem = stack.getItem();
        if (!(stackItem instanceof ItemPristineMatter))
            return MetadataDataModel.INVALID;

        return ((ItemPristineMatter) stackItem).getDataModelMetadata();
    }
}
