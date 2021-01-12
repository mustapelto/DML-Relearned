package mustapelto.deepmoblearning.common.mobdata.twilight;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
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
        super(EnumMobType.TWILIGHT_GLACIER.getName(),
                0,
                33,
                5,
                13,
                EnumLivingMatterType.TWILIGHT,
                256,
                new String[]{
                        "twilightforest:yeti_alpha",
                        "twilightforest:yeti",
                        "twilightforest:winter_wolf",
                        "twilightforest:penguin",
                        "twilightforest:snow_guardian",
                        "twilightforest:stable_ice_core",
                        "twilightforest:unstable_ice_core",
                        "twilightforest:snow_queen"
                }
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
