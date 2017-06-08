package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.world.World;

public class EndermanMetaData extends MobMetaData {
    private static EndermanMetaData instance;

    private EndermanMetaData() {
        super("Enderman",
                "Endermen",
                20,
                30,
                5,
                11,
                EnumLivingMatterType.EXTRATERRESTRIAL,
                new String[]{"Friendly unless provoked, dislikes rain.", "Teleports short distances."}
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
