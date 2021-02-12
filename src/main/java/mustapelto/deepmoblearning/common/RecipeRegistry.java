package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.metadata.MobMetadataManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class RecipeRegistry {
    @SubscribeEvent
    public static void registerEvent(RegistryEvent.Register<IRecipe> event) {
        DMLRelearned.logger.info("Registering Dynamic Recipes...");
        IForgeRegistry<IRecipe> registry = event.getRegistry();
        registry.registerAll(MobMetadataManager.getCraftingRecipes());
    }
}
