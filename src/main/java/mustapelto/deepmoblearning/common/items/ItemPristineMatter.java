package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

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
        return I18n.format("deepmoblearning.pristine_matter.display_name", metadata.getDisplayName());
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
