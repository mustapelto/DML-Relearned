package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.World;

public class CreeperMetaData extends MobMetaData {
    private static CreeperMetaData instance;

    private CreeperMetaData() {
        super("Creeper",
                10,
                42,
                5,
                5,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"Will blow up your base if", "left unattended."}
                );
    }

    public static CreeperMetaData getInstance() {
        if (instance == null)
            instance = new CreeperMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityCreeper(world);
    }
}
