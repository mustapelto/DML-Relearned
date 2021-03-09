package mustapelto.deepmoblearning.common.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions") // IDE complains about PLAYER_TRIAL_CAPABILITY being always null
public class PlayerTrialProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(IPlayerTrial.class)
    public static final Capability<IPlayerTrial> PLAYER_TRIAL_CAPABILITY = null;

    private final IPlayerTrial instance = PLAYER_TRIAL_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PLAYER_TRIAL_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == PLAYER_TRIAL_CAPABILITY ? PLAYER_TRIAL_CAPABILITY.cast(instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return PLAYER_TRIAL_CAPABILITY.getStorage().writeNBT(PLAYER_TRIAL_CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        PLAYER_TRIAL_CAPABILITY.getStorage().readNBT(PLAYER_TRIAL_CAPABILITY, instance, null, nbt);
    }
}
