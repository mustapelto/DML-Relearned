package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.mobdata.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SkeletonMetaData extends MobMetaData {
    private static SkeletonMetaData instance;

    private SkeletonMetaData() {
        super(EnumMobType.SKELETON.getName(),
                10,
                38,
                6,
                10,
                EnumLivingMatterType.OVERWORLDIAN,
                80,
                new String[]{
                        "minecraft:stray",
                        "minecraft:skeleton",
                        "twilightforest:skeleton_druid"
                }
                );
    }

    public static SkeletonMetaData getInstance() {
        if (instance == null)
            instance = new SkeletonMetaData();
        return instance;
    }

    @Override
    public Entity getEntity(World world) {
        EntitySkeleton entity = new EntitySkeleton(world);
        entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BOW));
        return entity;
    }
}
