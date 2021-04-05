package mustapelto.deepmoblearning.common.tiles;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.GuiTrialOverlay;
import mustapelto.deepmoblearning.common.inventory.ContainerTileEntity;
import mustapelto.deepmoblearning.common.inventory.ContainerTrialKeystone;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerTrialKey;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageStartTrial;
import mustapelto.deepmoblearning.common.network.MessageTrialOverlayMessage;
import mustapelto.deepmoblearning.common.trials.AttunementData;
import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import mustapelto.deepmoblearning.common.util.PlayerHelper;
import mustapelto.deepmoblearning.common.util.TrialKeyHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class TileEntityTrialKeystone extends TileEntityContainer implements ITickable {
    private final ItemHandlerTrialKey trialKey = new ItemHandlerTrialKey();
    private final Set<EntityPlayerMP> participants = Collections.newSetFromMap(new WeakHashMap<>());

    private ItemStack activeTrialKey = ItemStack.EMPTY;
    private AttunementData activeTrialData;
    private int currentWave;
    private int mobsDefeated;
    private int mobsSpawned;


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

    public void startTrial() {
        if (!canStartTrial())
            return;

        if (world.isRemote)
            DMLPacketHandler.sendToServer(new MessageStartTrial(this));
        else {
            markDirty();

            activeTrialKey = getTrialKey().copy();
            // trialKey.setStackInSlot(0, ItemStack.EMPTY);
            activeTrialData = TrialKeyHelper.getAttunement(activeTrialKey).orElse(null);
            if (activeTrialData == null) {
                // Invalid Trial Key -> abort Trial
                stopTrial();
                return;
            }

            participants.addAll(
                    PlayerHelper.getLivingPlayersInArea(
                            world,
                            pos,
                            DMLConstants.TrialKeystone.TRIAL_AREA_RADIUS,
                            DMLConstants.TrialKeystone.TRIAL_AREA_HEIGHT,
                            0
                    )
            );
            participants.forEach(player -> DMLPacketHandler.sendToClientPlayer(
                    new MessageTrialOverlayMessage(GuiTrialOverlay.OverlayMessage.COMPLETED),
                    player
            ));
            setDefaultTrialState();
        }
    }

    public void stopTrial() {
        setDefaultTrialState();
    }

    //
    // Inventory
    //

    @Override
    public ContainerTileEntity getContainer(InventoryPlayer inventoryPlayer) {
        return new ContainerTrialKeystone(this, inventoryPlayer);
    }

    public ItemStack getTrialKey() {
        return trialKey.getStackInSlot(0);
    }

    public boolean hasTrialKey() {
        return ItemStackHelper.isTrialKey(getTrialKey());
    }

    //
    // Trial state and conditions
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

    private boolean canStartTrial() {
        return hasTrialKey() && TrialKeyHelper.isAttuned(getTrialKey()) && !isTrialActive() && isTrialAreaClear();
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getLastWave() {
        return activeTrialData != null ? activeTrialData.getMaxWave() : 0;
    }

    public int getMobsDefeated() {
        return mobsDefeated;
    }

    public int getWaveMobTotal() {
        return activeTrialData != null ? activeTrialData.getCurrentWaveMobTotal(currentWave) : 0;
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
    // RENDER
    //

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos(), getPos().add(1, 2, 1));
    }


    //
    // SERVER/CLIENT SYNC
    //


    @Override
    public ByteBuf getUpdateData() {
        return super.getUpdateData();
    }

    //
    // CAPABILITIES
    //

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
                super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(trialKey);

        return super.getCapability(capability, facing);
    }

    //
    // NBT WRITE/READ
    //

    // NBT Tag Names
    private static final String NBT_TRIAL_KEY = "trialKey";
    private static final String NBT_TRIAL_STATE = "trialState";
    private static final String NBT_ACTIVE_TRIAL_KEY = "activeTrialKey";
    private static final String NBT_CURRENT_WAVE = "currentWave";
    private static final String NBT_MOBS_DEFEATED = "mobsDefeated";

    // Legacy NBT Tag Names
    private static final String NBT_LEGACY_TRIAL_KEY = "inventory";

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagCompound inventory = new NBTTagCompound();
        inventory.setTag(NBT_TRIAL_KEY, trialKey.serializeNBT());
        compound.setTag(NBT_INVENTORY, inventory);

        NBTTagCompound trialState = new NBTTagCompound();
        NBTTagCompound activeTrialKeyNBT = new NBTTagCompound();
        activeTrialKey.writeToNBT(activeTrialKeyNBT);
        trialState.setTag(NBT_ACTIVE_TRIAL_KEY, activeTrialKeyNBT);
        compound.setTag(NBT_TRIAL_STATE, trialState);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (isLegacyNBT(compound)) {
            // Original DML tag -> read Trial Key from legacy inventory tag and set Trial State to default values
            trialKey.deserializeNBT(compound.getCompoundTag(NBT_LEGACY_TRIAL_KEY));
            setDefaultTrialState();
        } else {
            // DML:Relearned tag -> use new (nested) tag names
            NBTTagCompound inventory = compound.getCompoundTag(NBT_INVENTORY);
            trialKey.deserializeNBT(inventory.getCompoundTag(NBT_TRIAL_KEY));
            readTrialStateFromNBT(compound.getCompoundTag(NBT_TRIAL_STATE));
        }
    }

    private void readTrialStateFromNBT(NBTTagCompound compound) {
        currentWave = compound.getInteger(NBT_CURRENT_WAVE);
        mobsDefeated = compound.getInteger(NBT_MOBS_DEFEATED);
        // If world is closed while Trial is running, already spawned but not defeated mobs will despawn. We can get them back by resetting the count to the number of defeated mobs.
        mobsSpawned = mobsDefeated;

        activeTrialKey = new ItemStack(compound.getCompoundTag(NBT_ACTIVE_TRIAL_KEY));
        activeTrialData = TrialKeyHelper.getAttunement(activeTrialKey).orElse(null);
        if (activeTrialData == null)
            stopTrial();
    }

    private void setDefaultTrialState() {
        participants.clear();
        activeTrialKey = ItemStack.EMPTY;
        activeTrialData = null;
        currentWave = -1;
        mobsDefeated = 0;
        mobsSpawned = 0;
    }

    private static boolean isLegacyNBT(NBTTagCompound nbt) {
        return !nbt.hasKey(NBT_TRIAL_STATE);
    }
}
