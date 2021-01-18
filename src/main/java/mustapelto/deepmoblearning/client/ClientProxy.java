package mustapelto.deepmoblearning.client;

import mustapelto.deepmoblearning.client.gui.DataOverlay;
import mustapelto.deepmoblearning.common.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy {
    @Override
    public void registerGuiRenderers() {
        MinecraftForge.EVENT_BUS.register(new DataOverlay(Minecraft.getMinecraft()));
    }
}
