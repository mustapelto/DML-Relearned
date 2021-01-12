package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;

public class SpiderMetaData extends MobMetaData.MobMetaDataExtra {
    private static SpiderMetaData instance;

    private SpiderMetaData() {
        super(EnumMobType.SPIDER.getName(),
                8,
                30,
                5,
                0,
                EnumLivingMatterType.OVERWORLDIAN,
                80,
                new String[]{
                        "minecraft:spider",
                        "minecraft:cave_spider",
                        "twilightforest:hedge_spider",
                        "twilightforest:king_spider",
                        "deepmoblearning:trial_spider",
                        "deepmoblearning:trial_cave_spider"
                },
                5,
                -25
                );
    }

    public static SpiderMetaData getInstance() {
        if (instance == null)
            instance = new SpiderMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntitySpider(world);
    }

    @Override
    public Entity getEntityExtra(World world) {
        return new EntityCaveSpider(world);
    }
}
