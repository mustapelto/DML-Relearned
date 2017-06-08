package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class WitchMetaData extends MobMetaData {
    private static WitchMetaData instance;

    private WitchMetaData() {
        super("Witch",
                "Witches",
                13,
                34,
                4,
                11,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"Affinity with potions and concoctions.", "Likes cats.", "Beware!"}
                );
    }

    public static WitchMetaData getInstance() {
        if (instance == null)
            instance = new WitchMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        EntityWitch entity = new EntityWitch(world);
        entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.POTIONITEM));
        return entity;
    }
}
