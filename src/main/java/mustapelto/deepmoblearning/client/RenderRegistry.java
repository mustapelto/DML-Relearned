package mustapelto.deepmoblearning.client;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.items.DMLItem;
import mustapelto.deepmoblearning.common.registry.RegistryHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderRegistry {
    @SubscribeEvent
    public static void register(ModelRegistryEvent event) {
        DMLRelearned.logger.info("Registering Models...");
        RegistryHandler.registeredItems.forEach(RenderRegistry::registerItem);
    }

    private static void registerItem(Item item) {
        if (!(item instanceof DMLItem))
            return; // This should never happen. Added mainly to silence IDE warnings.

        ModelResourceLocation modelResourceLocation;

        // Check if model file with item's name exists. If not, use appropriate default model.
        if (!((DMLItem) item).getModelFileExists()) {
            // No valid model file --> use default model
            modelResourceLocation = new ModelResourceLocation(((DMLItem) item).getDefaultResourceLocation(), "inventory");
        } else {
            // Valid model file --> use it
            ResourceLocation location = item.getRegistryName();
            if (location == null)
                return;
            modelResourceLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
        }

        // Register model
        ModelLoader.setCustomModelResourceLocation(item, 0, modelResourceLocation);
    }
}
