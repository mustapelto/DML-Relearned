package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.world.World;

public class GhastMetaData extends MobMetaData {
    private static GhastMetaData instance;

    private GhastMetaData() {
        super(EnumMobType.GHAST.getName(),
                5,
                10,
                0,
                -20,
                EnumLivingMatterType.HELLISH,
                372,
                new String[]{"minecraft:ghast"}
                );
    }

    public static GhastMetaData getInstance() {
        if (instance == null)
            instance = new GhastMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityGhast(world);
    }
}
