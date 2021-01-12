package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.world.World;

public class EnderDragonMetaData extends MobMetaData {
    private static EnderDragonMetaData instance;

    private EnderDragonMetaData() {
        super(EnumMobType.ENDER_DRAGON.getName(),
                100,
                7,
                0,
                -20,
                EnumLivingMatterType.EXTRATERRESTRIAL,
                2560,
                new String[]{"minecraft:ender_dragon"}
                );
    }

    public static EnderDragonMetaData getInstance() {
        if (instance == null)
            instance = new EnderDragonMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityDragon(world);
    }
}
