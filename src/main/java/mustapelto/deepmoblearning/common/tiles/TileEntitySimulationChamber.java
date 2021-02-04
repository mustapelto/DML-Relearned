package mustapelto.deepmoblearning.common.tiles;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.energy.DMLEnergyStorage;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerDataModel;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerInputWrapper;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerOutput;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerPolymerClay;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.items.ItemPolymerClay;
import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageRequestUpdateSimChamber;
import mustapelto.deepmoblearning.common.network.MessageUpdateSimChamber;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class TileEntitySimulationChamber extends TileEntityRedstoneControlled implements ITickable {
    private final DMLEnergyStorage energyStorage = new DMLEnergyStorage(DMLConstants.SimulationChamber.ENERGY_CAPACITY, DMLConstants.SimulationChamber.ENERGY_IN_MAX);

    private final ItemHandlerDataModel inputDataModel = new ItemHandlerDataModel() {
        @Override
        protected void onContentsChanged(int slot) {
            onDataModelChanged();
        }
    };
    private final ItemHandlerInputWrapper dataModelWrapper = new ItemHandlerInputWrapper(inputDataModel);
    private final ItemHandlerPolymerClay inputPolymer = new ItemHandlerPolymerClay();
    private final ItemHandlerInputWrapper polymerWrapper = new ItemHandlerInputWrapper(inputPolymer);
    private final ItemHandlerOutput outputLiving = new ItemHandlerOutput();
    private final ItemHandlerOutput outputPristine = new ItemHandlerOutput();

    private final SimulationState simulationState = new SimulationState(); // Stores state of simulation (running, progress, pristine success)
    private final SimulationState oldState = new SimulationState().set(simulationState); // Copy of state for comparison (mark for disk save if changed)
    private int oldEnergy = 0;

    private long lastUpdateSent = -1; // Time when last update was sent to client
    private boolean guiOpen = false; // Request update every tick while GUI is open

    //TODO: sidedness config

    @Override
    public void update() {
        // While GUI is open, request state update from server every tick to update GUI
        if (world.isRemote && guiOpen) {
            requestUpdateFromServer();
        }

        if (!world.isRemote) {
            // Send state update to client every 2 seconds
            long currentTime = world.getTotalWorldTime();
            if (lastUpdateSent == -1 || (currentTime - lastUpdateSent > 40)) {
                lastUpdateSent = currentTime;
                sendUpdateToClient();
            }

            if (!simulationState.isSimulationRunning() && canStartSimulation()) {
                startSimulation();
            }

            if (simulationState.isSimulationRunning()) {
                if (canContinueSimulation()) {
                    // Enough energy for processing -> void energy and advance process
                    energyStorage.voidEnergy(getSimulationEnergyCost());
                    simulationState.advanceSimulationProgress();
                }

                if (simulationState.isSimulationFinished()) {
                    finishSimulation();
                }
            }

            // Check if simulation state or energy content has changed and mark for disk save if it has
            if (!simulationState.equals(oldState) || energyStorage.getEnergyStored() != oldEnergy) {
                markDirty();
                oldState.set(simulationState);
                oldEnergy = energyStorage.getEnergyStored();
            }
        }
    }

    /**
     * Start simulation (only called once all requirements are met)
     */
    private void startSimulation() {
        simulationState.setSimulationRunning(true);

        // Calculate Pristine Matter success
        int pristineChance = DataModelHelper.getPristineChance(getDataModel());
        int random = ThreadLocalRandom.current().nextInt(100);
        simulationState.setPristineSuccess(random < pristineChance);

        // Consume Polymer Clay
        getPolymerClay().shrink(1);
    }

    private boolean canStartSimulation() {
        return hasDataModel() && hasPolymerClay() && canDataModelSimulate() && hasEnergyForSimulation() && !isLivingMatterOutputFull() && !isPristineMatterOutputFull() && isRedstoneActive();
    }

    private boolean canContinueSimulation() {
        return hasEnergyForSimulation() && isRedstoneActive();
    }

    /**
     * Increase Living and Pristine Matter output; reset simulation state
     */
    private void finishSimulation() {
        MobMetadata mobMetaData = DataModelHelper.getMobMetadata(getDataModel());
        if (mobMetaData == null)
            return;
        DataModelHelper.addSimulation(getDataModel());

        ItemStack oldLivingMatterOutput = outputLiving.getStackInSlot(0);
        if (!oldLivingMatterOutput.isEmpty()) {
            oldLivingMatterOutput.grow(1);
        } else {
            ItemStack newLivingMatterOutput = mobMetaData.getLivingMatterData().getItemStack(oldLivingMatterOutput.getCount() + 1);
            outputLiving.setStackInSlot(0, newLivingMatterOutput);
        }

        if (simulationState.isPristineSuccess()) {
            ItemStack oldPristineMatterOutput = outputPristine.getStackInSlot(0);
            if (!oldPristineMatterOutput.isEmpty()) {
                oldPristineMatterOutput.grow(1);
            } else {
                ItemStack newPristineMatterOutput = mobMetaData.getPristineMatter(oldPristineMatterOutput.getCount() + 1);
                outputPristine.setStackInSlot(0, newPristineMatterOutput);
            }
        }

        simulationState.reset();
    }

    /**
     * (Server only) Reset simulation state on data model change. Send updated data to client.
     */
    private void onDataModelChanged() {
        if (!world.isRemote) {
            simulationState.reset();
            sendUpdateToClient();
        }
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

    public int getSimulationEnergyCost() {
        return DataModelHelper.getSimulationEnergy(getDataModel());
    }

    public boolean hasEnergyForSimulation() {
        return energyStorage.getEnergyStored() >= getSimulationEnergyCost();
    }

    public SimulationState getSimulationState() {
        return simulationState;
    }

    public ItemStack getDataModel() {
        return inputDataModel.getStackInSlot(0);
    }

    public boolean hasDataModel() {
        return getDataModel().getItem() instanceof ItemDataModel;
    }

    public boolean canDataModelSimulate() {
        return DataModelHelper.canSimulate(getDataModel());
    }

    public ItemStack getPolymerClay() {
        return inputPolymer.getStackInSlot(0);
    }

    public boolean hasPolymerClay() {
        return getPolymerClay().getItem() instanceof ItemPolymerClay;
    }

    public boolean isLivingMatterOutputFull() {
        ItemStack livingMatterStack = outputLiving.getStackInSlot(0);

        if (livingMatterStack.isEmpty())
            return false;

        boolean stackIsFull = (livingMatterStack.getCount() == outputLiving.getSlotLimit(0));
        boolean stackMatchesDataModel = DataModelHelper.getDataModelMatchesLivingMatter(getDataModel(), livingMatterStack);

        return (stackIsFull || !stackMatchesDataModel);
    }

    public boolean isPristineMatterOutputFull() {
        ItemStack pristineMatterStack = outputPristine.getStackInSlot(0);

        if (pristineMatterStack.isEmpty())
            return false;

        boolean stackIsFull = (pristineMatterStack.getCount() == outputPristine.getSlotLimit(0));
        boolean stackMatchesDataModel = DataModelHelper.getDataModelMatchesPristineMatter(getDataModel(), pristineMatterStack);

        return (stackIsFull || !stackMatchesDataModel);
    }

    //
    // SERVER/CLIENT SYNC
    //

    private void sendUpdateToClient() {
        DMLPacketHandler.sendToClient(new MessageUpdateSimChamber(this), world, pos);
    }

    private void requestUpdateFromServer() {
        DMLPacketHandler.sendToServer(new MessageRequestUpdateSimChamber(this));
    }

    public void setGuiState(boolean open) {
        guiOpen = open;
    }

    public void setState(int energy, SimulationState simulationState) {
        energyStorage.setEnergy(energy);
        this.simulationState.set(simulationState);
    }

    //
    // NBT WRITE/READ
    //

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean("dml-relearned", true);
        compound.setTag("inputDataModel", inputDataModel.serializeNBT());
        compound.setTag("inputPolymer", inputPolymer.serializeNBT());
        compound.setTag("outputLiving", outputLiving.serializeNBT());
        compound.setTag("outputPristine", outputPristine.serializeNBT());
        compound.setTag("simulationState", simulationState.serializeNBT());
        compound.setInteger("energy", energyStorage.getEnergyStored());

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey("dml-relearned")) { // Original DML tag -> use old tag system
            inputDataModel.deserializeNBT(compound.getCompoundTag("dataModel"));
            inputPolymer.deserializeNBT(compound.getCompoundTag("polymer"));
            outputLiving.deserializeNBT(compound.getCompoundTag("lOutput"));
            outputPristine.deserializeNBT(compound.getCompoundTag("pOutput"));
            simulationState.setSimulationRunning(NBTHelper.getBoolean(compound, "isCrafting", false));
            simulationState.setPristineSuccess(NBTHelper.getBoolean(compound, "craftSuccess", false));
            simulationState.setSimulationProgress(NBTHelper.getInteger(compound, "simulationProgress", 0));
        } else { // DML:Relearned tag -> use new tag system
            inputDataModel.deserializeNBT(compound.getCompoundTag("inputDataModel"));
            inputPolymer.deserializeNBT(compound.getCompoundTag("inputPolymer"));
            outputLiving.deserializeNBT(compound.getCompoundTag("outputLiving"));
            outputPristine.deserializeNBT(compound.getCompoundTag("outputPristine"));
            simulationState.deserializeNBT(compound.getCompoundTag("simulationState"));
        }
        if (compound.hasKey("energy", Constants.NBT.TAG_INT))
            energyStorage.setEnergy(compound.getInteger("energy"));

        super.readFromNBT(compound);
    }

    //
    // CAPABILITIES
    //

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
                (capability == CapabilityEnergy.ENERGY) ||
                super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                        new CombinedInvWrapper(inputDataModel, inputPolymer, outputLiving, outputPristine));
            else
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                        new CombinedInvWrapper(dataModelWrapper, polymerWrapper, outputLiving, outputPristine));
        } else if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(@Nonnull World world, @Nonnull BlockPos pos, IBlockState oldState, IBlockState newState) {
        return (oldState.getBlock() != newState.getBlock());
    }

    /**
     * Class for storing and comparing state of simulation
     */
    public static class SimulationState {
        private boolean simulationRunning;
        private int simulationProgress;
        private boolean pristineSuccess;

        private static final String SIMULATION_RUNNING = "simulationRunning";
        private static final String SIMULATION_PROGRESS = "simulationProgress";
        private static final String PRISTINE_SUCCESS = "pristineSuccess";

        public SimulationState() {
            this(false, 0, false);
        }

        public SimulationState(boolean simulationRunning, int simulationProgress, boolean pristineSuccess) {
            this.simulationRunning = simulationRunning;
            this.simulationProgress = simulationProgress;
            this.pristineSuccess = pristineSuccess;
        }

        public SimulationState set(SimulationState simulationState) {
            this.simulationRunning = simulationState.simulationRunning;
            this.simulationProgress = simulationState.simulationProgress;
            this.pristineSuccess = simulationState.pristineSuccess;
            return this;
        }

        public void reset() {
            simulationRunning = false;
            simulationProgress = 0;
            pristineSuccess = false;
        }

        public void setSimulationProgress(int simulationProgress) {
            this.simulationProgress = simulationProgress;
        }

        public float getSimulationRelativeProgress() {
            return (float)simulationProgress / DMLConfig.GENERAL_SETTINGS.SIMULATION_CHAMBER_PROCESSING_TIME;
        }

        public void advanceSimulationProgress() {
            simulationProgress++;
        }

        public boolean isSimulationFinished() {
            return simulationProgress >= DMLConfig.GENERAL_SETTINGS.SIMULATION_CHAMBER_PROCESSING_TIME;
        }

        public boolean isSimulationRunning() {
            return simulationRunning;
        }

        public void setSimulationRunning(boolean simulationRunning) {
            this.simulationRunning = simulationRunning;
        }

        public boolean isPristineSuccess() {
            return pristineSuccess;
        }

        public void setPristineSuccess(boolean pristineSuccess) {
            this.pristineSuccess = pristineSuccess;
        }

        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean(SIMULATION_RUNNING, simulationRunning);
            nbt.setInteger(SIMULATION_PROGRESS, simulationProgress);
            nbt.setBoolean(PRISTINE_SUCCESS, pristineSuccess);
            return nbt;
        }

        public void deserializeNBT(NBTTagCompound nbt) {
            simulationRunning = nbt.hasKey(SIMULATION_RUNNING) && nbt.getBoolean(SIMULATION_RUNNING);
            simulationProgress = nbt.hasKey(SIMULATION_PROGRESS) ? nbt.getInteger(SIMULATION_PROGRESS) : 0;
            pristineSuccess = nbt.hasKey(PRISTINE_SUCCESS) && nbt.getBoolean(PRISTINE_SUCCESS);
        }

        public ByteBuf toBytes() {
            ByteBuf buf = Unpooled.buffer();
            buf.writeBoolean(simulationRunning);
            buf.writeInt(simulationProgress);
            buf.writeBoolean(pristineSuccess);
            return buf;
        }

        public SimulationState fromBytes(ByteBuf buf) {
            simulationRunning = buf.readBoolean();
            simulationProgress = buf.readInt();
            pristineSuccess = buf.readBoolean();
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimulationState that = (SimulationState) o;
            return simulationRunning == that.simulationRunning && simulationProgress == that.simulationProgress && pristineSuccess == that.pristineSuccess;
        }

        @Override
        public int hashCode() {
            return Objects.hash(simulationRunning, simulationProgress, pristineSuccess);
        }
    }
}
