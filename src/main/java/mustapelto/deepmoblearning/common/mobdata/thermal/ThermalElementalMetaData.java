package mustapelto.deepmoblearning.common.mobdata.thermal;

import cofh.thermalfoundation.entity.monster.EntityBlizz;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ThermalElementalMetaData extends MobMetaData {
    private static ThermalElementalMetaData instance;

    private ThermalElementalMetaData() {
        super(EnumMobType.THERMAL_ELEMENTAL.getName(),
                10,
                48,
                10,
                20,
                EnumLivingMatterType.OVERWORLDIAN,
                256,
                new String[]{
                        "thermalfoundation:blizz",
                        "thermalfoundation:blitz",
                        "thermalfoundation:basalz"
                }
                );
    }

    public static ThermalElementalMetaData getInstance() {
        if (instance == null)
            instance = new ThermalElementalMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        return new EntityBlizz(world);
    }
}
