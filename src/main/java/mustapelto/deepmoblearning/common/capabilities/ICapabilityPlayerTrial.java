package mustapelto.deepmoblearning.common.capabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public interface ICapabilityPlayerTrial {
    BlockPos getTrialKeystonePos();
    void setTrialKeystonePos(BlockPos trialKeystonePos);

    int getCurrentWave();
    void setCurrentWave(int currentWave);

    int getLastWave();
    void setLastWave(int lastWave);

    int getMobsDefeated();
    void setMobsDefeated(int mobsDefeated);

    int getWaveMobTotal();
    void setWaveMobTotal(int waveMobTotal);

    boolean isTrialActive();
    void setTrialActive(boolean trialActive);

    void reset();
    void copy(ICapabilityPlayerTrial original);

    void toBytes(ByteBuf buf);
    void fromBytes(ByteBuf buf);

    void syncToClient(EntityPlayerMP player);
}
