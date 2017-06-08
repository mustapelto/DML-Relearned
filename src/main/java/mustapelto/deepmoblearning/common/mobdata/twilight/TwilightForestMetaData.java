package mustapelto.deepmoblearning.common.mobdata.twilight;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import twilightforest.entity.boss.EntityTFLich;

public class TwilightForestMetaData extends MobMetaData.MobMetaDataExtra {
    private static TwilightForestMetaData instance;

    private TwilightForestMetaData() {
        super("Forest creature", 0,
                35,
                6,
                12,
                EnumLivingMatterType.TWILIGHT,
                new String[]{"Nagas, Liches and flying books.", "What the hell have you walked into?"},
                0,
                0,
                "Gain data by defeating non-vanilla mobs in the Naga Courtyard and Lich Tower."
                );
    }

    public static TwilightForestMetaData getInstance() {
        if (instance == null)
            instance = new TwilightForestMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityTFLich(world);
    }

    @Override
    public Entity getEntityExtra(World world) {
        return null;
    }
}
