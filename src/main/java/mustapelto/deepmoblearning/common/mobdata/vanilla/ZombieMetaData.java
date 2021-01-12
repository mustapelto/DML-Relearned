package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

public class ZombieMetaData extends MobMetaData.MobMetaDataExtra {
    private static ZombieMetaData instance;

    private ZombieMetaData() {
        super(EnumMobType.ZOMBIE.getName(),
                10,
                35,
                -2,
                6,
                EnumLivingMatterType.OVERWORLDIAN,
                80,
                new String[]{
                        "minecraft:husk",
                        "minecraft:zombie",
                        "minecraft:zombie_villager",
                        "minecraft:zombie_pigman"
                },
                21,
                6
                );
    }

    public static ZombieMetaData getInstance() {
        if (instance == null)
            instance = new ZombieMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityZombie(world);
    }

    @Override
    public Entity getEntityExtra(World world) {
        EntityZombie entity = new EntityZombie(world);
        entity.setChild(true);
        return entity;
    }
}
