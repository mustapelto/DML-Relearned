package mustapelto.deepmoblearning.common.mobdata.vanilla;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
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
        super("Skeleton",
                10,
                38,
                6,
                10,
                EnumLivingMatterType.OVERWORLDIAN,
                new String[]{"A formidable archer, which seems to be running", "some sort of cheat engine.", "A shield could prove useful."}
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
