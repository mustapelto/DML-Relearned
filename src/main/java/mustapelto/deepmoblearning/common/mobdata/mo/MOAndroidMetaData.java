package mustapelto.deepmoblearning.common.mobdata.mo;

import matteroverdrive.entity.monster.EntityMeleeRougeAndroidMob;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class MOAndroidMetaData extends MobMetaData {
    private static MOAndroidMetaData instance;

    private MOAndroidMetaData() {
        super(EnumMobType.MO_ANDROID.getName(),
                0,
                33,
                4,
                8,
                EnumLivingMatterType.OVERWORLDIAN,
                256,
                new String[]{
                        "matteroverdrive:ranged_rogue_android",
                        "matteroverdrive:rogue_android"
                }
                );
    }

    public static MOAndroidMetaData getInstance() {
        if (instance == null)
            instance = new MOAndroidMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityMeleeRougeAndroidMob(world);
    }
}
