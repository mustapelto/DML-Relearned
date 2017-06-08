package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.world.World;

public class ShulkerMetaData extends MobMetaData {
    private static ShulkerMetaData instance;

    private ShulkerMetaData() {
        super("Shulker",
                15,
                36,
                5,
                -5,
                EnumLivingMatterType.EXTRATERRESTRIAL,
                new String[]{"Found in End cities.", "Sneaky little buggers."}
                );
    }

    public static ShulkerMetaData getInstance() {
        if (instance == null)
            instance = new ShulkerMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityShulker(world);
    }
}
