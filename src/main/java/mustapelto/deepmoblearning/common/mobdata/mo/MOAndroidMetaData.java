package mustapelto.deepmoblearning.common.mobdata.mo;

import matteroverdrive.entity.monster.EntityMeleeRougeAndroidMob;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class MOAndroidMetaData extends MobMetaData {
    private static MOAndroidMetaData instance;

    private MOAndroidMetaData() {
        super("Rogue Android",
                0,
                33,
                4,
                8,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"It's not simply an android.", "It's a life form, entirely unique.", "Meep morp."}
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
