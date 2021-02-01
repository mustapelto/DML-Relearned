package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemPristineMatter extends ItemBase {
    private final MobMetadata metadata;

    public ItemPristineMatter(MobMetadata metadata) {
        super("pristine_matter_" + metadata.getItemID(), 64, metadata.isModLoaded());
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
}
