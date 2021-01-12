package mustapelto.deepmoblearning.common.mobdata.twilight;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import twilightforest.entity.boss.EntityTFMinoshroom;

public class TwilightSwampMetaData extends MobMetaData {
    private static TwilightSwampMetaData instance;

    private TwilightSwampMetaData() {
        super(EnumMobType.TWILIGHT_SWAMP.getName(),
                0,
                33,
                6,
                14,
                EnumLivingMatterType.TWILIGHT,
                256,
                new String[]{
                        "twilightforest:minotaur",
                        "twilightforest:minoshroom",
                        "twilightforest:maze_slime",
                        "twilightforest:fire_beetle",
                        "twilightforest:pinch_beetle",
                        "twilightforest:slime_beetle",
                        "twilightforest:hydra"
                }
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
}
