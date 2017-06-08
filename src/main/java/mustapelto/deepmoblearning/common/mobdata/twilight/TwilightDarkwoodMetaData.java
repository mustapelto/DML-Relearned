package mustapelto.deepmoblearning.common.mobdata.twilight;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import twilightforest.entity.boss.EntityTFUrGhast;

public class TwilightDarkwoodMetaData extends MobMetaData.MobMetaDataExtra {
    private static TwilightDarkwoodMetaData instance;

    private TwilightDarkwoodMetaData() {
        super("Darkwood creature",
                0,
                3,
                -3,
                -3,
                EnumLivingMatterType.TWILIGHT,
                new String[]{"Spooky scary strongholds send shivers down", "your spine, the Ur-Ghast will shock your", "soul and seal your doom tonight!"},
                0,
                0,
                "Gain data by defeating non-vanilla mobs in the Goblin Knight Stronghold and Dark Tower."
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

    @Override
    public Entity getEntityExtra(World world) {
        return null;
    }
}
