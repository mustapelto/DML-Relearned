package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.world.World;

public class WitherMetaData extends MobMetaData {
    private static WitherMetaData instance;

    private WitherMetaData() {
        super("Wither",
                150,
                22,
                3,
                18,
                EnumLivingMatterType.EXTRATERRESTRIAL,
                new String[]{"Do not approach this enemy. Run!", "I mean it has 3 heads, what could", "possibly go wrong?"}
                );
    }

    public static WitherMetaData getInstance() {
        if (instance == null)
            instance = new WitherMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityWither(world);
    }
}
