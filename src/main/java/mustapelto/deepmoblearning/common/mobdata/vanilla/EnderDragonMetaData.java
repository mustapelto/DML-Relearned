package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.world.World;

public class EnderDragonMetaData extends MobMetaData {
    private static EnderDragonMetaData instance;

    private EnderDragonMetaData() {
        super("Ender Dragon",
                100,
                7,
                0,
                -20,
                EnumLivingMatterType.EXTRATERRESTRIAL,
                new String[]{"Resides in the End, does not harbor treasure.", "Destroy its crystals, break the cycle!"}
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
