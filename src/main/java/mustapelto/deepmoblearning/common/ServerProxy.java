package mustapelto.deepmoblearning.common;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ServerProxy {
    // Client-only methods
    public void registerGuiRenderers() {}
    public void spawnSmokeParticle(World world, double x, double y, double z, double mx, double my, double mz, SmokeType type) {}

    public enum SmokeType { SMOKE, MIXED, CYAN }

    public String getLocalizedString(String key, Object... args) {
        return new TextComponentTranslation(key, args).toString();
    }
}
