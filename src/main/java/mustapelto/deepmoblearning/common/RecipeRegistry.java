package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModelTiers;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModels;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerLivingMatter;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class RecipeRegistry {
    @SubscribeEvent
    public static void registerEvent(RegistryEvent.Register<IRecipe> event) {
        // Finalize Data Managers (initialize ItemStack lists and crafting recipes)
        MetadataManagerDataModels.INSTANCE.finalizeData();
        MetadataManagerDataModelTiers.INSTANCE.finalizeData();
        MetadataManagerLivingMatter.INSTANCE.finalizeData();

        DMLRelearned.logger.info("Registering Dynamic Recipes...");
        IForgeRegistry<IRecipe> registry = event.getRegistry();
        registry.registerAll(MetadataManagerDataModels.INSTANCE.getCraftingRecipes().toArray(new IRecipe[0]));
    }
}
