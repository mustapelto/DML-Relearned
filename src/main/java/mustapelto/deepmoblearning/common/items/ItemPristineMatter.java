package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemPristineMatter extends ItemBase {
    private final MobMetadata mobMetadata;

    public ItemPristineMatter(MobMetadata mobMetadata) {
        super(mobMetadata.getPristineMatterName(), 64, mobMetadata.isModLoaded());
        this.mobMetadata = mobMetadata;
    }

    @Override
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return true; // Make items glow
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return I18n.format("deepmoblearning.pristine_matter.display_name", mobMetadata.getDisplayName());
    }

    private MobMetadata getMobMetadata() {
        return mobMetadata;
    }

    public static MobMetadata getMobMetadata(ItemStack stack) {
        Item stackItem = stack.getItem();
        if (!(stackItem instanceof ItemPristineMatter))
            return null;

        return ((ItemPristineMatter) stackItem).getMobMetadata();
    }
}
