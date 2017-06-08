package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;

public class SlimeMetaData extends MobMetaData {
    private static SlimeMetaData instance;

    private SlimeMetaData() {
        super("Slime",
                8,
                60,
                10,
                -16,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"The bouncing bouncer", "bounces his bouncy bouncing", "Bouncing and bou- squish! - \"A new slime haiku\""}
                );
    }

    public static SlimeMetaData getInstance() {
        if (instance == null)
            instance = new SlimeMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntitySlime(world);
    }
}
