package mustapelto.deepmoblearning.common.capabilities;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class PlayerTrial implements IPlayerTrial {
    private int currentWave = 0;
    private int lastWave = 0;
    private int mobsDefeated = 0;
    private int waveMobTotal = 0;
    private BlockPos trialKeystonePos = null;
    private boolean trialActive = false;

    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerTrial.class, new PlayerTrialStorage(), PlayerTrial::new);
    }

    @Override
    public int getCurrentWave() {
        return currentWave;
    }

    @Override
    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

    @Override
    public int getLastWave() {
        return lastWave;
    }

    @Override
    public void setLastWave(int lastWave) {
        this.lastWave = lastWave;
    }

    @Override
    public int getMobsDefeated() {
        return mobsDefeated;
    }

    @Override
    public void setMobsDefeated(int mobsDefeated) {
        this.mobsDefeated = mobsDefeated;
    }

    @Override
    public int getWaveMobTotal() {
        return waveMobTotal;
    }

    @Override
    public void setWaveMobTotal(int waveMobTotal) {
        this.waveMobTotal = waveMobTotal;
    }

    @Override
    public BlockPos getTrialKeystonePos() {
        return trialKeystonePos;
    }

    @Override
    public void setTrialKeystonePos(BlockPos trialKeystonePos) {
        this.trialKeystonePos = trialKeystonePos;
    }

    @Override
    public boolean isTrialActive() {
        return trialActive;
    }

    @Override
    public void setTrialActive(boolean trialActive) {
        this.trialActive = trialActive;
    }
}
