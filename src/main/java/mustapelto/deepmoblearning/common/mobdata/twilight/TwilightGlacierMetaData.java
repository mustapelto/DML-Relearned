package mustapelto.deepmoblearning.common.mobdata.twilight;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import twilightforest.entity.boss.EntityTFSnowQueen;

public class TwilightGlacierMetaData extends MobMetaData {
    private static TwilightGlacierMetaData instance;

    private TwilightGlacierMetaData() {
        super("Glacier inhabitant",
                0,
                33,
                5,
                13,
                EnumLivingMatterType.TWILIGHT,
                new String[]{"Here you'll find caves with ancient beasts", "and Elsa's wicked distant cousin Aurora.", "(Elsa might \"let it go\", but Aurora sure won't!)"},
                "Gain data by defeating non-vanilla mobs in the Yeti Lair and Ice Tower."
                );
    }

    public static TwilightGlacierMetaData getInstance() {
        if (instance == null)
            instance = new TwilightGlacierMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        EntityTFSnowQueen entity = new EntityTFSnowQueen(world);
        entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.SNOWBALL));
        return entity;
    }
}
