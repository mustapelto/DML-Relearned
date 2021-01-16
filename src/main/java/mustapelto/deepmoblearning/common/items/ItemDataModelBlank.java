package mustapelto.deepmoblearning.common.items;

import net.minecraft.util.ResourceLocation;

public class ItemDataModelBlank extends DMLItem {
    public ItemDataModelBlank() {
        super("data_model_blank", 64);
    }

    @Override
    public ResourceLocation getDefaultResourceLocation() {
        return null; // Non-generic item -> this should never be called
    }
}
