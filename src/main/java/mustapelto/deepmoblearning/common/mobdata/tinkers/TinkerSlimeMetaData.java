package mustapelto.deepmoblearning.common.mobdata.tinkers;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import slimeknights.tconstruct.world.entity.EntityBlueSlime;

public class TinkerSlimeMetaData extends MobMetaData {
    private static TinkerSlimeMetaData instance;

    private TinkerSlimeMetaData() {
        super("Blue slime",
                8,
                60,
                10,
                -16,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"The elusive blue slime. Seemingly a", "part of some sort of power hierarchy,", "since there's a bunch of \"King slimes\" around."}
                );
    }

    public static TinkerSlimeMetaData getInstance() {
        if (instance == null)
            instance = new TinkerSlimeMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityBlueSlime(world);
    }
}
