package mustapelto.deepmoblearning.common.mobdata.twilight;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import twilightforest.entity.boss.EntityTFMinoshroom;

public class TwilightSwampMetaData extends MobMetaData.MobMetaDataExtra {
    private static TwilightSwampMetaData instance;

    private TwilightSwampMetaData() {
        super("Swamp creature",
                0,
                33,
                6,
                14,
                EnumLivingMatterType.TWILIGHT,
                new String[]{"This realm sure could use some building regulations.", "How are you even allowed to build a huge maze", "in your basement!?"},
                0,
                0,
                "Gain data by defeating non-vanilla mobs in the Swamp Labyrinth and Hydra Lair."
                );
    }

    public static TwilightSwampMetaData getInstance() {
        if (instance == null)
            instance = new TwilightSwampMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        EntityTFMinoshroom entity = new EntityTFMinoshroom(world);
        Item axe = Item.getByNameOrId("twilightforest:minotaur_axe");
        entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(axe != null ? axe : Items.DIAMOND_AXE));
        return entity;
    }

    @Override
    public Entity getEntityExtra(World world) {
        return null;
    }
}
