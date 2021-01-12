package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.world.World;

public class GuardianMetaData extends MobMetaData {
    private static GuardianMetaData instance;

    private GuardianMetaData() {
        super(EnumMobType.GUARDIAN.getName(),
                15,
                36,
                5,
                -5,
                EnumLivingMatterType.OVERWORLDIAN,
                340,
                new String[]{
                        "minecraft:elder_guardian",
                        "minecraft:guardian"
                }
                );
    }

    public static GuardianMetaData getInstance() {
        if (instance == null)
            instance = new GuardianMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityGuardian(world);
    }
}
