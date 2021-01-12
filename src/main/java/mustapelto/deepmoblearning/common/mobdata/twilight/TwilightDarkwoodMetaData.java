package mustapelto.deepmoblearning.common.mobdata.twilight;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import twilightforest.entity.boss.EntityTFUrGhast;

public class TwilightDarkwoodMetaData extends MobMetaData {
    private static TwilightDarkwoodMetaData instance;

    private TwilightDarkwoodMetaData() {
        super(EnumMobType.TWILIGHT_DARKWOOD.getName(),
                0,
                3,
                -3,
                -3,
                EnumLivingMatterType.TWILIGHT,
                256,
                new String[]{
                        "twilightforest:redcap",
                        "twilightforest:blockchain_goblin",
                        "twilightforest:kobold",
                        "twilightforest:goblin_knight_lower",
                        "twilightforest:goblin_knight_upper",
                        "twilightforest:helmet_crab",
                        "twilightforest:knight_phantom",
                        "twilightforest:tower_ghast",
                        "twilightforest:tower_broodling",
                        "twilightforest:tower_golem",
                        "twilightforest:tower_termite",
                        "twilightforest:mini_ghast",
                        "twilightforest:ur_ghast"
                }
                );
    }

    public static TwilightDarkwoodMetaData getInstance() {
        if (instance == null)
            instance = new TwilightDarkwoodMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityTFUrGhast(world);
    }
}
