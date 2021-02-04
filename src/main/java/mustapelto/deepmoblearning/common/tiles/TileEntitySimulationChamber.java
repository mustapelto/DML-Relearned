package mustapelto.deepmoblearning.common.tiles;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerDataModel;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerInputWrapper;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerOutput;
import mustapelto.deepmoblearning.common.inventory.ItemHandlerPolymerClay;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.items.ItemPolymerClay;
import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class TileEntitySimulationChamber extends TileEntityMachine {
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

    private boolean pristineSuccess = false;

    //TODO: sidedness config

    public TileEntitySimulationChamber() {
        super(DMLConstants.SimulationChamber.ENERGY_CAPACITY, DMLConstants.SimulationChamber.ENERGY_IN_MAX);
    }

    //
    // Crafting
    //

    @Override
    protected void startCrafting() {
        super.startCrafting();

        // Calculate Pristine Matter success
        int pristineChance = DataModelHelper.getPristineChance(getDataModel());
        int random = ThreadLocalRandom.current().nextInt(100);
        pristineSuccess = (random < pristineChance);

        // Consume Polymer Clay
        getPolymerClay().shrink(1);
    }

    @Override
    protected boolean canStartCrafting() {
        return super.canStartCrafting() && hasDataModel() && hasPolymerClay() && canDataModelSimulate() && !isLivingMatterOutputFull() && !isPristineMatterOutputFull();
    }

    @Override
    protected void finishCrafting() {
        super.finishCrafting();

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
    }

    @Override
    protected void resetCrafting() {
        super.resetCrafting();
        pristineSuccess = false;
    }

    /**
     * (Server only) Reset simulation state on data model change.
     */
    private void onDataModelChanged() {
        if (!world.isRemote) {
            resetCrafting();
        }
    }

    @Override
    protected int getCraftingDuration() {
        return DMLConfig.GENERAL_SETTINGS.SIMULATION_CHAMBER_PROCESSING_TIME;
    }

    public boolean isPristineSuccess() {
        return pristineSuccess;
    }

    //
    // Energy
    //

    @Override
    public int getCraftingEnergyCost() {
        return DataModelHelper.getSimulationEnergy(getDataModel());
    }

    //
    // Inventory
    //

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
    // CAPABILITIES
    //

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
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
        }

        return super.getCapability(capability, facing);
    }

    //
    // CLIENT/SERVER SYNC
    //

    @Override
    public ByteBuf getUpdateData() {
        ByteBuf buf = super.getUpdateData();

        buf.writeBoolean(pristineSuccess);

        return buf;
    }

    @Override
    public void handleUpdateData(ByteBuf buf) {
        super.handleUpdateData(buf);

        pristineSuccess = buf.readBoolean();
    }

    //
    // NBT WRITE/READ
    //

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setTag(INPUT_DATA_MODEL, inputDataModel.serializeNBT());
        compound.setTag(INPUT_POLYMER, inputPolymer.serializeNBT());
        compound.setTag(OUTPUT_LIVING, outputLiving.serializeNBT());
        compound.setTag(OUTPUT_PRISTINE, outputPristine.serializeNBT());

        compound.getCompoundTag("crafting").setBoolean("pristineSuccess", pristineSuccess);

        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (!compound.hasKey(DML_RELEARNED)) { // Original DML tag -> use old tag system with machine-specific progress tag
            inputDataModel.deserializeNBT(compound.getCompoundTag(DATA_MODEL_OLD));
            inputPolymer.deserializeNBT(compound.getCompoundTag(POLYMER_OLD));
            outputLiving.deserializeNBT(compound.getCompoundTag(LIVING_OLD));
            outputPristine.deserializeNBT(compound.getCompoundTag(PRISTINE_OLD));

            crafting = NBTHelper.getBoolean(compound, IS_CRAFTING_OLD, false);
            craftingProgress = NBTHelper.getInteger(compound, CRAFTING_PROGRESS_OLD, 0);
            pristineSuccess = NBTHelper.getBoolean(compound, PRISTINE_SUCCESS_OLD, false);

        } else { // DML:Relearned tag -> use new tag system
            inputDataModel.deserializeNBT(compound.getCompoundTag(INPUT_DATA_MODEL));
            inputPolymer.deserializeNBT(compound.getCompoundTag(INPUT_POLYMER));
            outputLiving.deserializeNBT(compound.getCompoundTag(OUTPUT_LIVING));
            outputPristine.deserializeNBT(compound.getCompoundTag(OUTPUT_PRISTINE));

            pristineSuccess = NBTHelper.getBoolean(compound.getCompoundTag(CRAFTING), PRISTINE_SUCCESS, false);
            // Crafting state and progress set in super method
        }
    }

    // NBT Tag Names
    private static final String DATA_MODEL_OLD = "dataModel";
    private static final String POLYMER_OLD = "polymer";
    private static final String LIVING_OLD = "lOutput";
    private static final String PRISTINE_OLD = "pOutput";
    private static final String IS_CRAFTING_OLD = "isCrafting";
    private static final String CRAFTING_PROGRESS_OLD = "simulationProgress";
    private static final String PRISTINE_SUCCESS_OLD = "craftSuccess";

    private static final String INPUT_DATA_MODEL = "inputDataModel";
    private static final String INPUT_POLYMER = "inputPolymer";
    private static final String OUTPUT_LIVING = "outputLiving";
    private static final String OUTPUT_PRISTINE = "outputPristine";
    private static final String PRISTINE_SUCCESS = "pristineSuccess";
}
