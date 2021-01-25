package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.MobMetaData;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemPristineMatter extends DMLItem {
    private final MobMetaData metaData;

    public ItemPristineMatter(MobMetaData metaData) {
        super("pristine_matter_" + metaData.getItemID(), 64, metaData.isModLoaded());
        this.metaData = metaData;
    }

    @Override
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return true; // Make items glow
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return I18n.format("deepmoblearning.pristine_matter.display_name", metaData.getDisplayName());
    }
}
