package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.world.World;

public class BlazeMetaData extends MobMetaData {
    private static BlazeMetaData instance;

    private BlazeMetaData() {
        super("Blaze",
                10,
                48,
                10,
                20,
                EnumLivingMatterType.HELLISH,
                new String[]{"Bring buckets of water, and watch in despair", "as it evaporates, and everything is on fire.", "You are on fire."}
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
