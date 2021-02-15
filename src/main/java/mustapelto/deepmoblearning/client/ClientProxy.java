package mustapelto.deepmoblearning.client;

import mustapelto.deepmoblearning.client.gui.DataOverlay;
import mustapelto.deepmoblearning.client.particles.ParticleScalableSmoke;
import mustapelto.deepmoblearning.common.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.concurrent.ThreadLocalRandom;

public class ClientProxy extends ServerProxy {
    @Override
    public void registerGuiRenderers() {
        MinecraftForge.EVENT_BUS.register(new DataOverlay(Minecraft.getMinecraft()));
    }

    public void spawnSmokeParticle(World world, double x, double y, double z, double mx, double my, double mz, SmokeType type) {
        float scale = 1.0f;

        switch (type) {
            case CYAN:
            case MIXED:
                scale = 1.4f;
                break;
            case SMOKE:
                scale = 1.6f;
        }

        Particle particle = new ParticleScalableSmoke(world, x, y, z, mx, my, mz, scale);

        switch (type) {
            case CYAN:
                setColorCyan(particle);
                break;
            case MIXED:
                setColorMixed(particle);
                break;
            case SMOKE:
                setColorSmoke(particle);
                break;
        }

        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    private void setColorMixed(Particle particle) {
        boolean spawnCyan = ThreadLocalRandom.current().nextInt(0, 3) == 0; // p = 1/3

        if (spawnCyan)
            setColorCyan(particle);
        else
            setColorGray(particle);
    }

    private void setColorSmoke(Particle particle) {
        boolean spawnRed = ThreadLocalRandom.current().nextInt(0, 3) == 0; // p = 1/3
        boolean spawnBlack = ThreadLocalRandom.current().nextInt(0, 4) == 0; // p = 1/4

        if (spawnBlack)
            particle.setRBGColorF(0.02f, 0.02f, 0.02f);
        else if (spawnRed)
            particle.setRBGColorF(0.29f, 0.05f, 0.01f);
        else
            setColorGray(particle);

    }

    private void setColorCyan(Particle particle) {
        particle.setRBGColorF(0.0f, 1.0f, 0.75f);
    }

    private void setColorGray(Particle particle) {
        particle.setRBGColorF(0.09f, 0.09f, 0.09f);
    }
}
