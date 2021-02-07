package mustapelto.deepmoblearning.common.tiles;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.GuiLootFabricator;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.inventory.*;
import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityLootFabricator extends TileEntityMachine {
    private final ItemHandlerPristineMatter inputPristineMatter = new ItemHandlerPristineMatter() {
        @Override
        protected void onPristineTypeChanged() {
            TileEntityLootFabricator.this.onPristineTypeChanged();
        }
    };
    private final ItemHandlerInputWrapper pristineMatterWrapper = new ItemHandlerInputWrapper(inputPristineMatter);
    private final ItemHandlerOutput output = new ItemHandlerOutput(16);

    private int outputItemIndex = -1; // index of selected output item in Pristine Matter's associated item list. -1 = no item selected

    public TileEntityLootFabricator() {
        super(DMLConstants.LootFabricator.ENERGY_CAPACITY, DMLConstants.LootFabricator.ENERGY_IN_MAX);
    }

    //
    // GUI
    //

    @Override
    public ContainerMachine getContainer(InventoryPlayer inventoryPlayer) {
        return new ContainerLootFabricator(this, inventoryPlayer);
    }

    @Override
    public GuiLootFabricator getGUI(EntityPlayer player, World world) {
        return new GuiLootFabricator(this, player, world);
    }

    //
    // CRAFTING
    //

    // Loot Fabricator consumes Pristine Matter only when crafting is finished
    // This allows to change output when crafting has already started
    // without losing Pristine Matter

    @Override
    protected boolean canStartCrafting() {
        return super.canStartCrafting() && hasPristineMatter() && hasRoomForOutput();
    }

    @Override
    protected void finishCrafting() {
        super.finishCrafting();

        ItemStack outputItem = getOutputItem();
        if (outputItem.isEmpty())
            return; // Crafting without selected output (shouldn't happen) i.e. something went wrong -> don't do anything

        output.addItemToAvailableSlots(outputItem);
        getPristineMatter().shrink(1);
    }

    @Override
    protected int getCraftingDuration() {
        return DMLConfig.GENERAL_SETTINGS.LOOT_FABRICATOR_PROCESSING_TIME;
    }

    @Override
    public int getCraftingEnergyCost() {
        return DMLConfig.GENERAL_SETTINGS.LOOT_FABRICATOR_RF_COST;
    }

    private ItemStack getOutputItem() {
        if (outputItemIndex == -1)
            return ItemStack.EMPTY;

        ItemStack pristineMatter = getPristineMatter();
        if (pristineMatter.isEmpty())
            return ItemStack.EMPTY;

        MobMetadata mobMetadata = ItemPristineMatter.getMobMetadata(pristineMatter);

        if (mobMetadata == null)
            return ItemStack.EMPTY;

        return mobMetadata.getLootItem(outputItemIndex);
    }

    //
    // INVENTORY
    //

    /**
     * (Server only) Reset crafting state on pristine matter change.
     */
    private void onPristineTypeChanged() {
        if (!world.isRemote) {
            outputItemIndex = -1;
            resetCrafting();
        }
    }

    public ItemStack getPristineMatter() {
        return inputPristineMatter.getStackInSlot(0);
    }

    public boolean hasPristineMatter() {
        return getPristineMatter().getItem() instanceof ItemPristineMatter;
    }

    public boolean hasRoomForOutput() {
        return output.hasRoomForItem(getOutputItem());
    }

    private void checkIsValidOutput() {
        if (getOutputItem().isEmpty()) {
            outputItemIndex = -1;
            resetCrafting();
        }
    }

    //
    // CAPABILITIES
    //

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
                super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                        new CombinedInvWrapper(inputPristineMatter, output)
                );
            else
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(
                        new CombinedInvWrapper(pristineMatterWrapper, output)
                );
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
        checkIsValidOutput();
    }

    //
    // NBT WRITE/READ
    //

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagCompound inventory = new NBTTagCompound();
        inventory.setTag(PRISTINE_INPUT, inputPristineMatter.serializeNBT());
        inventory.setTag(OUTPUT, output.serializeNBT());
        compound.setTag(INVENTORY, inventory);

        compound.getCompoundTag(CRAFTING).setInteger(OUTPUT_ITEM_INDEX, outputItemIndex);

        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (isOldTagSystem(compound)) {
            // Original DML tag -> use old (non-nested) tag names
            inputPristineMatter.deserializeNBT(compound.getCompoundTag(PRISTINE_OLD));
            output.deserializeNBT(compound.getCompoundTag(OUTPUT));

            // Old system stores ItemStack, too much hassle to read from that so we set to "nothing".
            // I.e. players will have to re-set all their loot fab outputs once after switching.
            outputItemIndex = -1;
        } else {
            // DML:Relearned tag -> use new (nested) tag names
            NBTTagCompound inventory = compound.getCompoundTag(INVENTORY);
            inputPristineMatter.deserializeNBT(inventory.getCompoundTag(PRISTINE_INPUT));
            output.deserializeNBT(inventory.getCompoundTag(OUTPUT));

            outputItemIndex = NBTHelper.getInteger(compound.getCompoundTag(CRAFTING), OUTPUT_ITEM_INDEX, -1);
            checkIsValidOutput();
        }
    }

    // NBT Tag Names
    private static final String PRISTINE_INPUT = "inputPristine";
    private static final String OUTPUT = "output";
    private static final String OUTPUT_ITEM_INDEX = "outputItemIndex";

    private static final String PRISTINE_OLD = "pristine";
}
