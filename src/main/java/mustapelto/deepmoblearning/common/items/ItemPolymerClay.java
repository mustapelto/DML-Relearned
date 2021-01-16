package mustapelto.deepmoblearning.common.items;

import net.minecraft.util.ResourceLocation;

public class ItemPolymerClay extends DMLItem {
    public ItemPolymerClay() {
        super("polymer_clay", 64);
    }

    @Override
    public ResourceLocation getDefaultResourceLocation() {
        return null; // Non-generic item -> this should never be called
    }
}
