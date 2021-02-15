package mustapelto.deepmoblearning.common.tiles;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.energy.DMLEnergyStorage;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityMachine extends TileEntityBase implements ITickable {
    // TODO: Sidedness config
    // TODO: Blockstates (running/idle/error)
    // Energy
    protected final DMLEnergyStorage energyStorage;

    // Redstone
    protected boolean redstonePowered;
    protected int redstoneLevel;
    protected RedstoneMode redstoneMode = RedstoneMode.ALWAYS_ON;

    // Crafting
    protected boolean crafting = false;
    protected int craftingProgress = 0;

    // UI
    private boolean guiOpen = false;

    public TileEntityMachine(int energyCapacity, int energyMaxReceive) {
        energyStorage = new DMLEnergyStorage(energyCapacity, energyMaxReceive);
    }

    // Client/Server Sync
    protected boolean progressChanged = false; // if true -> server will respond to client's request with update packet

    //
    // ITickable
    //

    @Override
    public void update() {
        if (world.isRemote && guiOpen) {
            // check if progress or energy state has changed on the server
            requestUpdatePacketFromServer();
            return;
        }

        if (!crafting && canStartCrafting()) {
            startCrafting();
        }

        if (crafting && canContinueCrafting()) {
            energyStorage.voidEnergy(getCraftingEnergyCost());
            advanceCraftingProgress();
        }
    }

    //
    // Crafting
    //

    protected void startCrafting() {
        crafting = true;
        sendUpdatePacketToClient();
    }

    /**
     * @return true if machine is redstone-activated and has enough energy
     */
    protected boolean canStartCrafting() {
        return canContinueCrafting();
    }

    /**
     * @return true if machine is redstone-activated and has enough energy
     */
    private boolean canContinueCrafting() {
        return isRedstoneActive() && hasEnergyForCrafting();
    }

    private void advanceCraftingProgress() {
        craftingProgress++;
        if (craftingProgress >= getCraftingDuration())
            finishCrafting();

        // not sending an update packet here
        // as the client doesn't have to know exact crafting progress
        // unless GUI is open (which is handled in update method)
    }

    protected abstract int getCraftingDuration();

    protected void finishCrafting() {
        resetCrafting();
    }

    protected void resetCrafting() {
        crafting = false;
        craftingProgress = 0;
        sendUpdatePacketToClient();
    }

    public float getRelativeCraftingProgress() {
        return (float)craftingProgress / getCraftingDuration();
    }

    public boolean isCrafting() {
        return crafting;
    }

    //
    // Energy
    //

    public boolean hasEnergyForCrafting() {
        return energyStorage.getEnergyStored() >= getCraftingEnergyCost();
    }

    public abstract int getCraftingEnergyCost();

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergy() {
        return energyStorage.getMaxEnergyStored();
    }

    //
    // Redstone Control
    //

    @Nonnull
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode mode) {
        redstoneMode = mode;
        sendUpdatePacketToClient();
    }

    public boolean isRedstoneActive() {
        return RedstoneMode.isActive(redstoneMode, redstonePowered);
    }

    //
    // GUI
    //

    public void setGuiOpen(boolean open) {
        this.guiOpen = open;
    }

    //
    // Capabilities
    //

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) ||
                super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }

        return super.getCapability(capability, facing);
    }

    //
    // Client / Server Sync
    //

    @Override
    public boolean clientNeedsUpdate() {
        return progressChanged || energyStorage.getNeedsUpdate();
    }

    @Override
    protected void sendUpdatePacketToClient() {
        progressChanged = false;
        super.sendUpdatePacketToClient();
    }

    @Override
    public ByteBuf getUpdateData() {
        ByteBuf buf = super.getUpdateData();
        energyStorage.writeToBuffer(buf);

        buf.writeInt(redstoneLevel);
        buf.writeBoolean(redstonePowered);
        buf.writeInt(redstoneMode.getIndex());

        buf.writeBoolean(crafting);
        buf.writeInt(craftingProgress);

        return buf;
    }

    @Override
    public void handleUpdateData(ByteBuf buf) {
        energyStorage.readFromBuffer(buf);

        redstoneLevel = buf.readInt();
        redstonePowered = buf.readBoolean();
        redstoneMode = RedstoneMode.byIndex(buf.readInt());

        crafting = buf.readBoolean();
        craftingProgress = buf.readInt();
    }

    //
    // Block Update
    //

    public void onBlockPlaced() {
        onNeighborChange();
    }

    public void onNeighborChange() {
        boolean oldRedstonePowerState = redstonePowered;
        redstoneLevel = world.isBlockIndirectlyGettingPowered(pos);
        redstonePowered = redstoneLevel > 0;

        if (redstonePowered != oldRedstonePowerState) {
            if (!world.isRemote) {
                sendUpdatePacketToClient();
            } else {
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    //
    // NBT Write / Read
    //

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean(DML_RELEARNED, true); // Used to recognize DML-Relearned NBT tags when reading

        energyStorage.writeToNBT(compound);

        NBTTagCompound redstoneTag = new NBTTagCompound();
        redstoneTag.setInteger(REDSTONE_LEVEL, redstoneLevel);
        redstoneTag.setBoolean(REDSTONE_POWERED, redstonePowered);
        redstoneTag.setInteger(REDSTONE_MODE, redstoneMode.getIndex());
        compound.setTag(REDSTONE, redstoneTag);

        NBTTagCompound craftingTag = new NBTTagCompound();
        craftingTag.setBoolean(IS_CRAFTING, crafting);
        craftingTag.setInteger(CRAFTING_PROGRESS, craftingProgress);
        compound.setTag(CRAFTING, craftingTag);

        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        energyStorage.readFromNBT(compound);

        NBTTagCompound redstoneTag = compound.getCompoundTag(REDSTONE);
        redstoneLevel = NBTHelper.getInteger(redstoneTag, REDSTONE_LEVEL, 0);
        redstonePowered = NBTHelper.getBoolean(redstoneTag, REDSTONE_POWERED, false);
        redstoneMode = RedstoneMode.byIndex(NBTHelper.getInteger(redstoneTag, REDSTONE_MODE, 0));

        if (isOldTagSystem(compound)) {
            // Original DML tag -> use old tag system without nesting and with machine-specific progress tag
            crafting = NBTHelper.getBoolean(compound, IS_CRAFTING, false);
            if (this instanceof TileEntitySimulationChamber)
                craftingProgress = NBTHelper.getInteger(compound, CRAFTING_PROGRESS_SIM_CHAMBER_OLD, 0);
            else if (this instanceof TileEntityLootFabricator)
                craftingProgress = NBTHelper.getInteger(compound, CRAFTING_PROGRESS_LOOT_FAB_OLD, 0);
        } else {
            // DML:Relearned tag -> use new tag system
            NBTTagCompound craftingTag = compound.getCompoundTag(CRAFTING);
            crafting = NBTHelper.getBoolean(craftingTag, IS_CRAFTING, false);
            craftingProgress = NBTHelper.getInteger(craftingTag, CRAFTING_PROGRESS, 0);
        }
    }

    protected boolean isOldTagSystem(NBTTagCompound compound) {
        return !compound.hasKey(DML_RELEARNED);
    }

    // NBT tag names
    private static final String DML_RELEARNED = "dml-relearned";

    private static final String REDSTONE = "redstone"; // Redstone state subtag
    private static final String REDSTONE_LEVEL = "level";
    private static final String REDSTONE_POWERED = "powered";
    private static final String REDSTONE_MODE = "mode";

    protected static final String CRAFTING = "crafting"; // Crafting state subtag
    private static final String IS_CRAFTING = "isCrafting"; // Old system uses same tag, only not nested
    private static final String CRAFTING_PROGRESS = "progress";

    protected static final String INVENTORY = "inventory"; // Inventory contents subtag (only used by subclasses)

    // Tag names from old mod, used for backwards compatibility
    private static final String CRAFTING_PROGRESS_SIM_CHAMBER_OLD = "simulationProgress";
    private static final String CRAFTING_PROGRESS_LOOT_FAB_OLD = "crafingProgress"; // SIC!
}
