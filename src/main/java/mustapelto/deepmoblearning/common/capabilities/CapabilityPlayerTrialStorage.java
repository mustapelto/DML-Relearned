package mustapelto.deepmoblearning.common.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CapabilityPlayerTrialStorage implements Capability.IStorage<ICapabilityPlayerTrial> {
    // NBT Tags
    private static final String CURRENT_WAVE = "currentWave";
    private static final String LAST_WAVE = "lastWave";
    private static final String MOBS_DEFEATED = "mobsDefeated";
    private static final String WAVE_MOB_TOTAL = "waveMobTotal";
    private static final String TRIAL_ACTIVE = "trialActive";
    private static final String TRIAL_KEYSTONE_POS = "trialKeystonePos";

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ICapabilityPlayerTrial> capability, ICapabilityPlayerTrial instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger(CURRENT_WAVE, instance.getCurrentWave());
        compound.setInteger(LAST_WAVE, instance.getLastWave());
        compound.setInteger(MOBS_DEFEATED, instance.getMobsDefeated());
        compound.setInteger(WAVE_MOB_TOTAL, instance.getWaveMobTotal());
        compound.setBoolean(TRIAL_ACTIVE, instance.isTrialActive());
        compound.setLong(TRIAL_KEYSTONE_POS, instance.getTrialKeystonePos().toLong());

        return compound;
    }

    @Override
    public void readNBT(Capability<ICapabilityPlayerTrial> capability, ICapabilityPlayerTrial instance, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound))
            return;

        NBTTagCompound compound = (NBTTagCompound) nbt;
        instance.setCurrentWave(compound.getInteger(CURRENT_WAVE));
        instance.setLastWave(compound.getInteger(LAST_WAVE));
        instance.setMobsDefeated(compound.getInteger(MOBS_DEFEATED));
        instance.setWaveMobTotal(compound.getInteger(WAVE_MOB_TOTAL));
        instance.setTrialActive(compound.getBoolean(TRIAL_ACTIVE));
        instance.setTrialKeystonePos(BlockPos.fromLong(compound.getLong(TRIAL_KEYSTONE_POS)));
    }
}
