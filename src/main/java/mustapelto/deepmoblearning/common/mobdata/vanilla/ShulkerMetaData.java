package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.world.World;

public class ShulkerMetaData extends MobMetaData {
    private static ShulkerMetaData instance;

    private ShulkerMetaData() {
        super(EnumMobType.SHULKER.getName(),
                15,
                36,
                5,
                -5,
                EnumLivingMatterType.EXTRATERRESTRIAL,
                256,
                new String[]{"minecraft:shulker"}
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
