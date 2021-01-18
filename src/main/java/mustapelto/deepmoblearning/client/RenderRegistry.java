package mustapelto.deepmoblearning.client;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.items.DMLItem;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.items.ItemLivingMatter;
import mustapelto.deepmoblearning.common.registry.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderRegistry {
    private static final ResourceLocation DATA_MODEL_DEFAULT = new ResourceLocation("deepmoblearning:data_model_default");
    private static final ResourceLocation PRISTINE_MATTER_DEFAULT = new ResourceLocation("deepmoblearning:pristine_matter_default");
    private static final ResourceLocation LIVING_MATTER_DEFAULT = new ResourceLocation("deepmoblearning:living_matter_default");

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        DMLRelearned.logger.info("Registering Models...");
        RegistryHandler.registeredItems.forEach(RenderRegistry::registerItemModel);
    }

    private static void registerItemModel(Item item) {
        if (!(item instanceof DMLItem))
            return; // This should never happen. Added to silence IDE warnings.

        if (item instanceof ItemDataModel) { // Data Models
            registerFromIdOrDefault((DMLItem) item, "data_model_", ((ItemDataModel) item).getMobMetaData().getItemID(), DATA_MODEL_DEFAULT);
        } else if (item instanceof ItemLivingMatter) { // Living Matter
            registerFromIdOrDefault((DMLItem) item, "living_matter_", ((ItemLivingMatter) item).getData().getItemID(), LIVING_MATTER_DEFAULT);
        } else { // Unique items
            ResourceLocation registryLocation = item.getRegistryName();
            if (registryLocation == null)
                return;
            ModelResourceLocation modelResourceLocation = new ModelResourceLocation(registryLocation, "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, modelResourceLocation);
        }
    }

    private static void registerFromIdOrDefault(DMLItem item, String fileBaseName, String itemID, ResourceLocation defaultLocation) {
        // Full path location for checking if resource exists
        ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "models/item/" + fileBaseName + itemID + ".json");

        // Normal location for model loading
        ResourceLocation locationFromRegistry = item.getRegistryName();

        if (locationFromRegistry == null)
            return; // Should never happen but prevents IDE warnings

        try {
            // Will throw FileNotFoundException if model file doesn't exist in mod jar or resource packs
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Model resource not found: {}. Using default model.", locationFromId.toString());
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(defaultLocation, "inventory"));
            return;
        }

        // File found -> use model from file
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(locationFromRegistry, "inventory"));
    }
}
