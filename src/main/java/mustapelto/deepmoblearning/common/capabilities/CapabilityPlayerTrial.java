package mustapelto.deepmoblearning.common.capabilities;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageTrialCapabilityData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public class CapabilityPlayerTrial implements ICapabilityPlayerTrial {
    private BlockPos trialKeystonePos;
    private int currentWave;
    private int lastWave;
    private int mobsDefeated;
    private int waveMobTotal;
    private boolean trialActive;

    @Override
    public BlockPos getTrialKeystonePos() {
        return trialKeystonePos;
    }

    @Override
    public void setTrialKeystonePos(BlockPos trialKeystonePos) {
        this.trialKeystonePos = trialKeystonePos;
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
    public boolean isTrialActive() {
        return trialActive;
    }

    @Override
    public void setTrialActive(boolean trialActive) {
        this.trialActive = trialActive;
    }

    @Override
    public void reset() {
        trialKeystonePos = null;
        currentWave = 0;
        lastWave = 0;
        mobsDefeated = 0;
        waveMobTotal = 0;
        trialActive = false;
    }

    @Override
    public void copy(ICapabilityPlayerTrial original) {
        if (!(original instanceof CapabilityPlayerTrial))
            return;

        CapabilityPlayerTrial cap = (CapabilityPlayerTrial) original;

        trialKeystonePos = cap.trialKeystonePos;
        currentWave = cap.currentWave;
        lastWave = cap.lastWave;
        mobsDefeated = cap.mobsDefeated;
        waveMobTotal = cap.waveMobTotal;
        trialActive = cap.trialActive;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(trialKeystonePos.toLong());
        buf.writeInt(currentWave);
        buf.writeInt(lastWave);
        buf.writeInt(mobsDefeated);
        buf.writeInt(waveMobTotal);
        buf.writeBoolean(trialActive);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        trialKeystonePos = BlockPos.fromLong(buf.readLong());
        currentWave = buf.readInt();
        lastWave = buf.readInt();
        mobsDefeated = buf.readInt();
        waveMobTotal = buf.readInt();
        trialActive = buf.readBoolean();
    }

    @Override
    public void syncToClient(EntityPlayerMP player) {
        DMLPacketHandler.sendToClientPlayer(new MessageTrialCapabilityData(this), player);
    }
}
