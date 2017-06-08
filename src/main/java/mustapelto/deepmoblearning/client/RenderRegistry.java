package mustapelto.deepmoblearning.client;

import mustapelto.deepmoblearning.common.registry.RegistryHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderRegistry {
    @SubscribeEvent
    public static void register(ModelRegistryEvent event) {
        RegistryHandler.registeredItems.forEach(RenderRegistry::registerItem);
    }

    private static void registerItem(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
