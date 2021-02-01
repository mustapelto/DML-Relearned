package mustapelto.deepmoblearning.common.tiles;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.energy.DMLEnergyStorage;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerBase;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerInputDataModel;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerInputPolymer;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerOutput;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageRequestUpdateSimChamber;
import mustapelto.deepmoblearning.common.network.MessageUpdateSimChamber;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

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
    private int simulationProgress = 0; // Successful ticks (i.e. had enough energy) since start of simulation

    private long lastUpdateSent = -1; // Time when last update was sent to client
    private boolean guiOpen = false; // Request update every tick while GUI is open

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

            if (!simulationRunning && canStartSimulation())
                startSimulation();

            if (simulationRunning) {
                if (hasEnergyForSimulation()) {
                    // Enough energy for processing -> void energy and advance process
                    energyStorage.voidEnergy(getSimulationEnergyCost());
                    simulationProgress++;
                }

                if (simulationProgress >= DMLConfig.GENERAL_SETTINGS.SIMULATION_CHAMBER_PROCESSING_TIME) {
                    finishSimulation();
                }
            }
        }
    }

    private void startSimulation() {
        simulationRunning = true;
        int pristineChance = DataModelHelper.getPristineChance(getDataModel());
        int random = ThreadLocalRandom.current().nextInt(100);
        pristineSuccess = random < pristineChance;
    }

    private void resetSimulation() {
        simulationRunning = false;
        simulationProgress = 0;
        pristineSuccess = false;
    }

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

        if (pristineSuccess) {
            ItemStack oldPristineMatterOutput = outputPristine.getStackInSlot(0);
            if (!oldPristineMatterOutput.isEmpty()) {
                oldPristineMatterOutput.grow(1);
            } else {
                ItemStack newPristineMatterOutput = mobMetaData.getPristineMatter(oldPristineMatterOutput.getCount() + 1);
                outputPristine.setStackInSlot(0, newPristineMatterOutput);
            }
        }

        resetSimulation();
    }

    private boolean canStartSimulation() {
        return hasDataModel() && canDataModelSimulate() && hasEnergyForSimulation() && !isLivingMatterOutputFull() && !isPristineMatterOutputFull();
    }

    private void onDataModelChanged() {
        resetSimulation();
        if (!world.isRemote)
            sendUpdateToClient();
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

    public boolean hasDataModel() {
        return getDataModel().getItem() instanceof ItemDataModel;
    }

    public boolean canDataModelSimulate() {
        return DataModelHelper.canSimulate(getDataModel());
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

    public void setGuiState(boolean open) {
        guiOpen = open;
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

    @Override
    public boolean shouldRefresh(@Nonnull World world, @Nonnull BlockPos pos, IBlockState oldState, IBlockState newState) {
        return (oldState.getBlock() != newState.getBlock());
    }
}
