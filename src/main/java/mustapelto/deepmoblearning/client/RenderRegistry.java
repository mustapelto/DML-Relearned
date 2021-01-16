package mustapelto.deepmoblearning.client;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.items.DMLItem;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
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
import java.util.ArrayList;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderRegistry {
    private static final ResourceLocation DATA_MODEL_DEFAULT = new ResourceLocation(DMLConstants.DataModel.DEFAULT_MODEL_NAME);

    @SubscribeEvent
    public static void register(ModelRegistryEvent event) {
        DMLRelearned.logger.info("Registering Models...");
        RegistryHandler.registeredItems.forEach(RenderRegistry::registerItem);
    }

    private static void registerItem(Item item) {
        if (!(item instanceof DMLItem))
            return; // This should never happen. Added mainly to silence IDE warnings.

        if (item instanceof ItemDataModel) {
            registerDataModel((ItemDataModel) item);
            return;
        }

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

    private static void registerDataModel(ItemDataModel dataModel) {
        ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "models/item/data_model_" + dataModel.getMobMetaData().getItemID() + ".json");
        ResourceLocation locationFromRegistry = dataModel.getRegistryName();
        if (locationFromRegistry == null)
            return;

        try {
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
        } catch (IOException e) {
            DMLRelearned.logger.info("Data Model resource not found: {}. Using default model.", locationFromId.toString());
            ModelLoader.setCustomModelResourceLocation(dataModel, 0, new ModelResourceLocation(DATA_MODEL_DEFAULT, "inventory"));
            return;
        }

        ModelLoader.setCustomModelResourceLocation(dataModel, 0, new ModelResourceLocation(locationFromRegistry, "inventory"));
    }
}
