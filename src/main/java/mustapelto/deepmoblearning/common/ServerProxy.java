package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.common.capabilities.ICapabilityPlayerTrial;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ServerProxy {
    public enum SmokeType { SMOKE, MIXED, CYAN }

    // Client-only methods
    public void registerGuiRenderers() {}
    public void spawnSmokeParticle(World world, double x, double y, double z, double mx, double my, double mz, SmokeType type) {}

    @Nullable
    public ICapabilityPlayerTrial getClientPlayerTrialCapability() {
        return null;
    }


    public String getLocalizedString(String key, Object... args) {
        return new TextComponentTranslation(key, args).toString();
    }
}
