package mustapelto.deepmoblearning.common.energy;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.util.MathHelper;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;

public class DMLEnergyStorage extends EnergyStorage {
    private boolean stateChanged = false;

    public DMLEnergyStorage(int capacity, int maxReceive) {
        super(capacity, maxReceive, 0);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = super.receiveEnergy(maxReceive, simulate);
        if (energyReceived != 0)
            stateChanged = true;
        return energyReceived;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    public void voidEnergy(int energy) {
        this.energy -= Math.min(this.energy, energy);
    }

    public boolean getNeedsUpdate() {
        boolean result = stateChanged;
        stateChanged = false;
        return result;
    }

    public void writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("energy", energy);
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        setEnergy(NBTHelper.getInteger(compound, "energy", 0));
    }

    public void writeToBuffer(ByteBuf buf) {
        buf.writeInt(energy);
    }

    public void readFromBuffer(ByteBuf buf) {
        setEnergy(buf.readInt());
    }

    private void setEnergy(int energy) {
        this.energy = MathHelper.Clamp(energy, 0, getMaxEnergyStored());
    }
}
