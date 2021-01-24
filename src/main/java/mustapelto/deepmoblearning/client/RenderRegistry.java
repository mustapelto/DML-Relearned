package mustapelto.deepmoblearning.client;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelDataModel;
import mustapelto.deepmoblearning.client.models.ModelLivingMatter;
import mustapelto.deepmoblearning.client.models.ModelPristineMatter;
import mustapelto.deepmoblearning.common.items.DMLItem;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.items.ItemLivingMatter;
import mustapelto.deepmoblearning.common.items.ItemPristineMatter;
import mustapelto.deepmoblearning.common.registry.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderRegistry {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        DMLRelearned.logger.info("Registering Models...");
        ModelLoaderRegistry.registerLoader(ModelDataModel.LoaderDataModel.INSTANCE);
        ModelLoaderRegistry.registerLoader(ModelPristineMatter.LoaderPristineMatter.INSTANCE);
        ModelLoaderRegistry.registerLoader(ModelLivingMatter.LoaderLivingMatter.INSTANCE);
        RegistryHandler.registeredItems.forEach(RenderRegistry::registerItemModel);
    }

    private static void registerItemModel(Item item) {
        if (!(item instanceof DMLItem))
            return; // This should never happen. Added to silence IDE warnings.

        ModelResourceLocation modelLocation;

        if (item instanceof ItemDataModel || item instanceof ItemPristineMatter || item instanceof ItemLivingMatter) {
            ResourceLocation registryName = item.getRegistryName();
            if (registryName == null)
                return;
            String registryItem = registryName.getResourcePath();
            modelLocation = new ModelResourceLocation(new ResourceLocation(DMLConstants.ModInfo.ID, registryItem), "inventory");
        } else { // Unique items
            ResourceLocation registryLocation = item.getRegistryName();
            if (registryLocation == null)
                return;
            modelLocation = new ModelResourceLocation(registryLocation, "inventory");
        }

        ModelLoader.setCustomModelResourceLocation(item, 0, modelLocation);
    }
}
