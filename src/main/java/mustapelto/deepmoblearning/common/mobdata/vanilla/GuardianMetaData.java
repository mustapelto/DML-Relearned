package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.world.World;

public class GuardianMetaData extends MobMetaData {
    private static GuardianMetaData instance;

    private GuardianMetaData() {
        super("Guardian",
                15,
                36,
                5,
                -5,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"Lurking in the oceans.", "Uses some sort of sonar beam as", "a means of attack."}
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
