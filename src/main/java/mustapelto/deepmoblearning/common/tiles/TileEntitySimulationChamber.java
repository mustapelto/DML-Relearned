package mustapelto.deepmoblearning.common.tiles;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.energy.DMLEnergyStorage;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerBase;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerInputDataModel;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerInputPolymer;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerOutput;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageRequestUpdateSimChamber;
import mustapelto.deepmoblearning.common.network.MessageUpdateSimChamber;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntitySimulationChamber extends TileEntity implements ITickable {
    private final DMLEnergyStorage energyStorage = new DMLEnergyStorage(DMLConstants.SimulationChamber.ENERGY_CAPACITY, DMLConstants.SimulationChamber.ENERGY_IN_MAX);

    private final ItemHandlerBase inputDataModel = new ItemHandlerInputDataModel() {
        @Override
        protected void onContentsChanged(int slot) {
            onDataModelChanged();
        }
    };
    private final ItemHandlerBase inputPolymer = new ItemHandlerInputPolymer();
    private final ItemHandlerBase outputLiving = new ItemHandlerOutput();
    private final ItemHandlerBase outputPristine = new ItemHandlerOutput();

    private boolean simulationRunning = false;
    private boolean pristineSuccess = false;
    private int simulationProgress = 0; // Ticks since start of simulation

    private long lastUpdateSent = -1; // Time when last update was sent to client
    private boolean containerOpen = false; // Is container open? (--> send update every tick instead of every 2 seconds)

    @Override
    public void update() {
        if (!world.isRemote) {
            long currentTime = world.getTotalWorldTime();
            // send update every 2 seconds (or every tick if container is open)
            if (containerOpen || lastUpdateSent == -1 || (currentTime - lastUpdateSent > 40)) {
                lastUpdateSent = currentTime;
                sendUpdateToClient();
            }
        }
    }

    private void onDataModelChanged() {

    }

    //
    // STATE GETTERS
    //

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergy() {
        return energyStorage.getMaxEnergyStored();
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }

    public int getSimulationProgress() {
        return simulationProgress;
    }

    public boolean isPristineSuccess() {
        return pristineSuccess;
    }

    public ItemStack getDataModel() {
        return inputDataModel.getStackInSlot(0);
    }

    /*private boolean canStartSimulation() {

    }

    private boolean dataModelChanged() {
        return currentDataModelData !=
    }*/

    //
    // SERVER/CLIENT SYNC
    //

    private void sendUpdateToClient() {
        DMLPacketHandler.network.sendToAllTracking(new MessageUpdateSimChamber(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
    }

    private void requestUpdateFromServer() {
        DMLPacketHandler.network.sendToServer(new MessageRequestUpdateSimChamber(this));
    }

    public void setContainerState(boolean open) {
        containerOpen = open;
    }

    public void setState(int energy, boolean simulationRunning, int simulationProgress, boolean pristineSuccess) {
        energyStorage.setEnergy(energy);
        this.simulationRunning = simulationRunning;
        this.simulationProgress = simulationProgress;
        this.pristineSuccess = pristineSuccess;
    }

    @Override
    public void onLoad() {
        if (world.isRemote) {
            requestUpdateFromServer();
        }
    }

    //
    // DISK WRITE/READ
    //

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean("dml-relearned", true);
        compound.setInteger("simulationProgress", simulationProgress);
        compound.setBoolean("simulationRunning", simulationRunning);
        compound.setBoolean("pristineSuccess", pristineSuccess);
        compound.setTag("inputDataModel", inputDataModel.serializeNBT());
        compound.setTag("inputPolymer", inputPolymer.serializeNBT());
        compound.setTag("outputLiving", outputLiving.serializeNBT());
        compound.setTag("outputPristine", outputPristine.serializeNBT());
        compound.setInteger("energy", energyStorage.getEnergyStored());

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey("dml-relearned")) { // Original DML tag -> use old tag names
            inputDataModel.deserializeNBT(compound.getCompoundTag("dataModel"));
            inputPolymer.deserializeNBT(compound.getCompoundTag("polymer"));
            outputLiving.deserializeNBT(compound.getCompoundTag("lOutput"));
            outputPristine.deserializeNBT(compound.getCompoundTag("pOutput"));
            if (compound.hasKey("isCrafting", Constants.NBT.TAG_BYTE))
                simulationRunning = compound.getBoolean("isCrafting");
            if (compound.hasKey("craftSuccess", Constants.NBT.TAG_BYTE))
                pristineSuccess = compound.getBoolean("craftSuccess");
        } else { // DML:Relearned tag -> use new tag names
            inputDataModel.deserializeNBT(compound.getCompoundTag("inputDataModel"));
            inputPolymer.deserializeNBT(compound.getCompoundTag("inputPolymer"));
            outputLiving.deserializeNBT(compound.getCompoundTag("outputLiving"));
            outputPristine.deserializeNBT(compound.getCompoundTag("outputPristine"));
            if (compound.hasKey("simulationRunning", Constants.NBT.TAG_BYTE))
                simulationRunning = compound.getBoolean("simulationRunning");
            if (compound.hasKey("pristineSuccess", Constants.NBT.TAG_BYTE))
                pristineSuccess = compound.getBoolean("pristineSuccess");
        }
        if (compound.hasKey("simulationProgress", Constants.NBT.TAG_INT))
            simulationProgress = compound.getInteger("simulationProgress");
        if (compound.hasKey("energy", Constants.NBT.TAG_INT))
            energyStorage.setEnergy(compound.getInteger("energy"));

        super.readFromNBT(compound);
    }

    //
    // CAPABILITIES
    //

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
                (capability == CapabilityEnergy.ENERGY) ||
                super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new CombinedInvWrapper(inputDataModel, inputPolymer, outputLiving, outputPristine));
        } else if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }

        return super.getCapability(capability, facing);
    }
}
