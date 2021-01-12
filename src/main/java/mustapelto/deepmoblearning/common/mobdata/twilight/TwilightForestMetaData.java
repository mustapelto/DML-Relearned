package mustapelto.deepmoblearning.common.mobdata.twilight;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import twilightforest.entity.boss.EntityTFLich;

public class TwilightForestMetaData extends MobMetaData {
    private static TwilightForestMetaData instance;

    private TwilightForestMetaData() {
        super(EnumMobType.TWILIGHT_FOREST.getName(),
                0,
                35,
                6,
                12,
                EnumLivingMatterType.TWILIGHT,
                256,
                new String[]{
                        "twilightforest:naga",
                        "twilightforest:lich_minion",
                        "twilightforest:lich",
                        "twilightforest:death_tome",
                        "twilightforest:swarm_spider"
                }
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
}
