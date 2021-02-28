package mustapelto.deepmoblearning.common.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class PlayerHelper {
    public static List<EntityPlayerMP> getLivingPlayersInArea(World world, BlockPos center, int radius, int height, int offsetY) {
        return world.getEntitiesWithinAABB(
                EntityPlayerMP.class,
                new AxisAlignedBB(
                        center.getX() - radius, center.getY() + offsetY, center.getZ() - radius,
                        center.getX() + radius, center.getY() + offsetY + height, center.getZ() + radius
                ),
                p -> !p.isDead
        );
    }
}
