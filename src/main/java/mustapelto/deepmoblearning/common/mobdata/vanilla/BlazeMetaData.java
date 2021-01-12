package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.world.World;

public class BlazeMetaData extends MobMetaData {
    private static BlazeMetaData instance;

    private BlazeMetaData() {
        super(EnumMobType.BLAZE.getName(),
                10,
                48,
                10,
                20,
                EnumLivingMatterType.HELLISH,
                256,
                new String[]{"minecraft:blaze"}
        );
    }

    public static BlazeMetaData getInstance() {
        if (instance == null)
            instance = new BlazeMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityBlaze(world);
    }
}
