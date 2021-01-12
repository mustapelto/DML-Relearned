package mustapelto.deepmoblearning.common.mobdata.tinkers;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import slimeknights.tconstruct.world.entity.EntityBlueSlime;

public class TinkerSlimeMetaData extends MobMetaData {
    private static TinkerSlimeMetaData instance;

    private TinkerSlimeMetaData() {
        super(EnumMobType.TINKER_SLIME.getName(),
                8,
                60,
                10,
                -16,
                EnumLivingMatterType.OVERWORLDIAN,
                256,
                new String[]{"tconstruct:blueslime"}
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
