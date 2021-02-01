package mustapelto.deepmoblearning.common.energy;

import net.minecraftforge.energy.EnergyStorage;

public class DMLEnergyStorage extends EnergyStorage {
    public DMLEnergyStorage(int capacity, int maxReceive) {
        super(capacity, maxReceive, 0);
    }

    public void voidEnergy(int energy) {
        this.energy -= Math.min(this.energy, energy);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
