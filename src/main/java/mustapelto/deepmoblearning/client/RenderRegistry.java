package mustapelto.deepmoblearning.client;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.registry.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderRegistry {
    @SubscribeEvent
    public static void register(ModelRegistryEvent event) {
        DMLRelearned.logger.info("Registering Models...");
        RegistryHandler.registeredItems.forEach(RenderRegistry::registerItem);
    }

    private static void registerItem(Item item) {
        if (item instanceof ItemDataModel) {
            ItemStack stack = new ItemStack(item);
            ItemMeshDefinition meshDefinition = ((ItemDataModel) item).getMeshDefinition();
            ModelLoader.setCustomMeshDefinition(item, meshDefinition);
            ModelBakery.registerItemVariants(item, meshDefinition.getModelLocation(stack));
        } else {
            ModelResourceLocation resourceLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
