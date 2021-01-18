package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.metadata.MobMetaData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

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

    public NonNullList<ItemStack> getLootTable() {
        return metaData.getLootItems();
    }
}
