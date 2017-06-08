package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

public class ZombieMetaData extends MobMetaData.MobMetaDataExtra {
    private static ZombieMetaData instance;

    private ZombieMetaData() {
        super("Zombie",
                10,
                35,
                -2,
                6,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"They go moan in the night.", "Does not understand the need for", "personal space."},
                21,
                6,
                ""
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
