package mustapelto.deepmoblearning.common.tiles;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.inventory.*;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.ItemStackHelper;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;

public class TileEntityLootFabricator extends TileEntityMachine {
    private final ItemHandlerPristineMatter inputPristineMatter = new ItemHandlerPristineMatter() {
        @Override
        protected void onContentsChanged(int slot) {
            if (world.isRemote || slot != 0)
                return;

            MetadataDataModel newMetadata = getPristineMatterMetadata().orElse(null);
            if (pristineMatterMetadata != newMetadata) {
                if (newMetadata != null) {
                    pristineMatterMetadata = newMetadata;
                    outputItemIndex = -1;
                }
                resetCrafting();
            }
        }
    };
    private final ItemHandlerInputWrapper pristineMatterWrapper = new ItemHandlerInputWrapper(inputPristineMatter);
    private final ItemHandlerOutput output = new ItemHandlerOutput(16);

    private MetadataDataModel pristineMatterMetadata;
    private int outputItemIndex = -1; // index of selected output item in Pristine Matter's associated item list. -1 = no item selected

    public TileEntityLootFabricator() {
        super(DMLConstants.LootFabricator.ENERGY_CAPACITY, DMLConstants.LootFabricator.ENERGY_IN_MAX);
    }

    //
    // CRAFTING
    //

    // Loot Fabricator consumes Pristine Matter only when crafting is finished
    // This allows to change output when crafting has already started
    // without losing Pristine Matter

    @Override
    protected boolean canStartCrafting() {
        return super.canStartCrafting() && hasPristineMatter() && hasRoomForOutput() && isValidOutputItem();
    }

    @Override
    protected void finishCrafting() {
        resetCrafting();

        ItemStack outputItem = getOutputItem();
        if (outputItem.isEmpty())
            return; // Crafting without selected output (shouldn't happen) i.e. something went wrong -> don't do anything

        output.addItemToAvailableSlots(outputItem);
        inputPristineMatter.voidItem();
    }

    @Override
    protected int getCraftingDuration() {
        return DMLConfig.MACHINE_SETTINGS.LOOT_FABRICATOR_PROCESSING_TIME;
    }

    @Override
    public int getCraftingEnergyCost() {
        return DMLConfig.MACHINE_SETTINGS.LOOT_FABRICATOR_RF_COST;
    }
    
    @Nullable
    public MetadataDataModel getPristineMatterMetadata() {
        return pristineMatterMetadata;
    }

    private ItemStack getOutputItem() {
        if (outputItemIndex == -1)
            return ItemStack.EMPTY;

        if (pristineMatterMetadata == null)
            return ItemStack.EMPTY;

        return pristineMatterMetadata.getLootItem(outputItemIndex);
    }

    @Override
    protected CraftingState updateCraftingState() {
        if (!crafting && !hasPristineMatter())
            return CraftingState.IDLE;
        else if (!canStartCrafting() || !canContinueCrafting())
            return CraftingState.ERROR;

        return CraftingState.RUNNING;
    }

    //
    // INVENTORY
    //


    @Override
    public ContainerMachine getContainer(InventoryPlayer inventoryPlayer) {
        return new ContainerLootFabricator(this, inventoryPlayer);
    }

    public boolean hasPristineMatter() {
        return ItemStackHelper.isPristineMatter(inputPristineMatter.getStackInSlot(0));
    }

    public boolean hasRoomForOutput() {
        return output.hasRoomForItem(getOutputItem());
    }

    public void setOutputItemIndex(int index) {
        if (outputItemIndex == index)
            return; // Index didn't change -> do nothing

        outputItemIndex = index;
        if (isInvalidOutputItemIndex())
            outputItemIndex = -1;

        resetCrafting(); // resetCrafting takes care of updating output index to client
    }

    public int getOutputItemIndex() {
        return outputItemIndex;
    }

    private boolean isInvalidOutputItemIndex() {
        return outputItemIndex != -1 && !isValidOutputItem();
    }

    private boolean isValidOutputItem() {
        return !getOutputItem().isEmpty();
    }

    //
    // CAPABILITIES
    //

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
                super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                        new CombinedInvWrapper(inputPristineMatter, output)
                );
            } else {
                if (!DMLConfig.MACHINE_SETTINGS.LEGACY_MACHINE_SIDEDNESS) {
                    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                            new CombinedInvWrapper(pristineMatterWrapper, output)
                    );
                } else {
                    if (facing == EnumFacing.UP) {
                        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inputPristineMatter);
                    } else {
                        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(output);
                    }
                }
            }
        }

        return super.getCapability(capability, facing);
    }

    //
    // CLIENT/SERVER SYNC
    //

    @Override
    public ByteBuf getUpdateData() {
        ByteBuf buf = super.getUpdateData();

        buf.writeInt(outputItemIndex);

        return buf;
    }

    @Override
    public void handleUpdateData(ByteBuf buf) {
        super.handleUpdateData(buf);
        outputItemIndex = buf.readInt();
    }

    //
    // NBT WRITE/READ
    //

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagCompound inventory = new NBTTagCompound();
        inventory.setTag(NBT_PRISTINE_INPUT, inputPristineMatter.serializeNBT());
        inventory.setTag(NBT_OUTPUT, output.serializeNBT());
        compound.setTag(NBT_INVENTORY, inventory);

        compound.getCompoundTag(NBT_CRAFTING).setInteger(NBT_OUTPUT_ITEM_INDEX, outputItemIndex);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey(NBT_LEGACY_PRISTINE)) {
            inputPristineMatter.deserializeNBT(compound.getCompoundTag(NBT_LEGACY_PRISTINE));
            output.deserializeNBT(compound.getCompoundTag(NBT_OUTPUT));

            // Old system stores ItemStack, too much hassle to read from that so we set to "nothing".
            // I.e. players will have to restart all their loot fabricators once after switching.
            outputItemIndex = -1;
        } else {
            NBTTagCompound inventory = compound.getCompoundTag(NBT_INVENTORY);
            inputPristineMatter.deserializeNBT(inventory.getCompoundTag(NBT_PRISTINE_INPUT));
            output.deserializeNBT(inventory.getCompoundTag(NBT_OUTPUT));

            outputItemIndex = NBTHelper.getInteger(compound.getCompoundTag(NBT_CRAFTING), NBT_OUTPUT_ITEM_INDEX, -1);
        }

        pristineMatterMetadata = inputPristineMatter.getPristineMatterMetadata().orElse(null);
        if (isInvalidOutputItemIndex())
            outputItemIndex = -1;

        if (outputItemIndex == -1)
            resetCrafting();
    }

    // NBT Tag Names
    private static final String NBT_PRISTINE_INPUT = "inputPristine";
    private static final String NBT_OUTPUT = "output";
    private static final String NBT_OUTPUT_ITEM_INDEX = "outputItemIndex";

    private static final String NBT_LEGACY_PRISTINE = "pristine";
}
