package mustapelto.deepmoblearning.common.capabilities;

import net.minecraft.util.math.BlockPos;

public interface IPlayerTrial {
    int getCurrentWave();
    void setCurrentWave(int currentWave);

    int getLastWave();
    void setLastWave(int lastWave);

    int getMobsDefeated();
    void setMobsDefeated(int mobsDefeated);

    int getWaveMobTotal();
    void setWaveMobTotal(int waveMobTotal);

    BlockPos getTrialKeystonePos();
    void setTrialKeystonePos(BlockPos trialKeystonePos);

    boolean isTrialActive();
    void setTrialActive(boolean trialActive);
}
