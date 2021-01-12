package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.world.World;

public class EndermanMetaData extends MobMetaData {
    private static EndermanMetaData instance;

    private EndermanMetaData() {
        super(EnumMobType.ENDERMAN.getName(),
                20,
                30,
                5,
                11,
                EnumLivingMatterType.EXTRATERRESTRIAL,
                512,
                new String[]{
                        "minecraft:enderman",
                        "minecraft:endermite",
                        "deepmoblearning:trial_enderman"
                }
                );
    }

    public static EndermanMetaData getInstance() {
        if (instance == null)
            instance = new EndermanMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityEnderman(world);
    }
}
