package mustapelto.deepmoblearning.common.entities;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.DMLRegistry;
import mustapelto.deepmoblearning.common.ServerProxy;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityItemGlitchFragment extends EntityItem {
    private int progress = 0;

    public EntityItemGlitchFragment(World worldIn) {
        super(worldIn);
    }

    public EntityItemGlitchFragment(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z);
        setItem(stack);
        setPickupDelay(15);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (isInWater()) {
            AxisAlignedBB box = new AxisAlignedBB(posX - 1, posY - 1, posZ - 1, posX + 1, posY + 1, posZ + 1);
            List<EntityItem> bbContainedItems = world.getEntitiesWithinAABB(EntityItem.class, box);
            EntityItem goldEntity = getAnyInList(bbContainedItems, new ItemStack(Items.GOLD_INGOT));
            EntityItem lapisEntity = getAnyInList(bbContainedItems, new ItemStack(Items.DYE, 1, 4));
            EntityItem glitchFragmentEntity = getAnyInList(bbContainedItems, new ItemStack(DMLRegistry.ITEM_GLITCH_FRAGMENT));

            progress++;

            boolean requiredEntitiesPresent = goldEntity != null && lapisEntity != null && glitchFragmentEntity != null;

            if (world.isRemote) {
                spawnFragmentParticles();

                if (requiredEntitiesPresent) {
                    for (int i = 0; i < 3; i++) {
                        spawnFragmentParticles();
                    }
                }
            }

            if (!requiredEntitiesPresent) {
                progress = 0;
                return;
            }

            if (!world.isRemote) {
                if (progress >= 35) {
                    shrink(goldEntity);
                    shrink(lapisEntity);
                    shrink(glitchFragmentEntity);

                    spawnIngot();
                }
            }
        }
    }

    @Nullable
    private static EntityItem getAnyInList(List<EntityItem> input, ItemStack filter) {
        return input.stream().filter(entityItem -> isEntityItemEqual(entityItem, filter)).findAny().orElse(null);
    }

    private static boolean isEntityItemEqual(EntityItem entityItem, ItemStack item2) {
        ItemStack item1 = entityItem.getItem();
        if (item1.isEmpty() || item2.isEmpty())
            return false;
        return item1.isItemEqual(item2);
    }

    private void shrink(EntityItem entityItem) {
        ItemStack item = entityItem.getItem();
        item.shrink(1);
        if (item.isEmpty())
            entityItem.setDead();
    }

    private void spawnFragmentParticles() {
        if (!world.isRemote)
            return;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        DMLRelearned.proxy.spawnSmokeParticle(world,
                posX + random.nextDouble(-0.25, 0.25),
                posY + random.nextDouble(-0.1, 0.8),
                posZ + random.nextDouble(-0.25, 0.25),
                random.nextDouble(-0.08, 0.08),
                random.nextDouble(-0.08, 0.22),
                random.nextDouble(-0.08, 0.08),
                ServerProxy.SmokeType.CYAN
        );
    }

    private void spawnIngot() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        EntityItem ingot = new EntityItem(world, posX, posY + 0.6, posZ, new ItemStack(DMLRegistry.ITEM_GLITCH_INGOT, 1));
        ingot.motionX = random.nextDouble(-0.2, 0.2);
        ingot.motionY = 0;
        ingot.motionZ = random.nextDouble(-0.2, 0.2);
        ingot.setDefaultPickupDelay();

        world.spawnEntity(ingot);
    }
}
