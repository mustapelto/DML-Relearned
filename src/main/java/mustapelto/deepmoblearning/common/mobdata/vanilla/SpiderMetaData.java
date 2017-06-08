package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;

public class SpiderMetaData extends MobMetaData.MobMetaDataExtra {
    private static SpiderMetaData instance;

    private SpiderMetaData() {
        super("Spider",
                8,
                30,
                5,
                0,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"Nocturnal douchebags, beware!", "Drops strands of string for some reason."},
                5,
                -25,
                ""
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
