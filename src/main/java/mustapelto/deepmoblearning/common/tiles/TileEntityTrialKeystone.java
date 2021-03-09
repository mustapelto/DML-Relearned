package mustapelto.deepmoblearning.common.tiles;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerTrialKey;
import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import mustapelto.deepmoblearning.common.util.PlayerHelper;
import mustapelto.deepmoblearning.common.util.TrialKeyHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class TileEntityTrialKeystone extends TileEntityBase implements ITickable {
    private final ItemHandlerTrialKey trialKey = new ItemHandlerTrialKey();
    private final Set<EntityPlayerMP> participants = Collections.newSetFromMap(new WeakHashMap<>());

    private ItemStack activeTrialKey = ItemStack.EMPTY;

    @Override
    public void update() {
        if (world.isRemote)
            return;

        if (isTrialActive()) {
            disableFlying();

        }
    }

    //
    // GUI called functions
    //

    public void tryStartTrial() {
        if (!hasTrialKey() || !isTrialAreaClear())
            return;

        activeTrialKey = getTrialKey();
        if (!TrialKeyHelper.isAttuned(activeTrialKey))
            return;

        participants.addAll(
                PlayerHelper.getLivingPlayersInArea(
                        world,
                        pos,
                        DMLConstants.TrialKeystone.TRIAL_AREA_RADIUS,
                        DMLConstants.TrialKeystone.TRIAL_AREA_HEIGHT,
                        0
                )
        );
    }

    //
    // Inventory
    //

    public ItemStack getTrialKey() {
        return trialKey.getStackInSlot(0);
    }

    public boolean hasTrialKey() {
        return ItemStackHelper.isTrialKey(getTrialKey());
    }

    //
    // Trial conditions
    //

    public boolean isTrialAreaClear() {
        int groundY = pos.getY() - 1;
        int keystoneY = pos.getY();
        int areaMaxY = pos.getY() + DMLConstants.TrialKeystone.TRIAL_AREA_HEIGHT;
        int areaMinX = pos.getX() - DMLConstants.TrialKeystone.TRIAL_AREA_RADIUS;
        int areaMaxX = pos.getX() + DMLConstants.TrialKeystone.TRIAL_AREA_RADIUS;
        int areaMinZ = pos.getZ() - DMLConstants.TrialKeystone.TRIAL_AREA_RADIUS;
        int areaMaxZ = pos.getZ() + DMLConstants.TrialKeystone.TRIAL_AREA_RADIUS;

        // Check if layer below Trial area is "ground"
        Iterable<BlockPos> groundLayer = BlockPos.getAllInBox(areaMinX, groundY, areaMinZ, areaMaxX, groundY, areaMaxZ);
        for(BlockPos blockPos : groundLayer) {
            if (!world.getBlockState(blockPos).isFullBlock())
                return false;
        }

        // Check if layers above Trial area are "air"
        Iterable<BlockPos> airLayer = BlockPos.getAllInBox(areaMinX, keystoneY, areaMinZ, areaMaxX, areaMaxY, areaMaxZ);
        for (BlockPos blockPos : airLayer) {
            if (blockPos.equals(this.pos))
                continue; // Skip Trial Keystone block

            IBlockState state = world.getBlockState(blockPos);
            Block block = state.getBlock();
            if (!block.isAir(state, world, blockPos))
                return false;
        }

        return true;
    }

    public boolean isTrialActive() {
        return !activeTrialKey.isEmpty();
    }

    //
    // Trial actions
    //

    private void disableFlying() {
        participants.forEach(p -> {
            if (!p.isDead && !p.capabilities.isCreativeMode && p.capabilities.allowFlying) {
                p.capabilities.allowFlying = false;
                p.capabilities.isFlying = false;
                p.sendPlayerAbilities();
            }
        });
    }

    private void removeDistantParticipants() {
        Iterator<EntityPlayerMP> iterator = participants.iterator();
        while (iterator.hasNext()) {
            EntityPlayerMP player = iterator.next();
            double distance = player.getDistanceSqToCenter(pos);
            if (distance > DMLConstants.TrialKeystone.TRIAL_ARENA_RADIUS) {

                player.sendMessage(new TextComponentTranslation("deepmoblearning.trial.message.player_left"));
                iterator.remove();
            }
        }
    }

    //
    // Server/Client sync
    //

    @Override
    public void handleUpdateData(ByteBuf buf) {

    }
}
