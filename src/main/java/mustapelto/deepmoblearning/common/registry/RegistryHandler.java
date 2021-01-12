package mustapelto.deepmoblearning.common.registry;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.enums.EnumMobType;
import mustapelto.deepmoblearning.common.items.*;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import net.minecraft.item.Item;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class RegistryHandler {
    public static final NonNullList<Item> registeredItems = NonNullList.create();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        DMLRelearned.logger.info("Registering Items...");
        // Misc Items
        registeredItems.add(new ItemPolymerClay());
        registeredItems.add(new ItemDataModelBlank());
        registeredItems.add(new ItemDeepLearner());

        registerLivingMatter();
        registerDataModels();

        IForgeRegistry<Item> registry = event.getRegistry();
        registeredItems.forEach(registry::register);
    }

    private static void registerLivingMatter() {
        for (EnumLivingMatterType livingMatterType : EnumLivingMatterType.values()) {
            if (livingMatterType.isVanilla() || DMLConstants.ModDependencies.isLoaded(livingMatterType.getModID())) {
                registeredItems.add(new ItemLivingMatter(livingMatterType));
            }
        }
    }

    private static void registerDataModels() {
        for (EnumMobType mobType : EnumMobType.values()) {
            if (mobType.isVanilla() || DMLConstants.ModDependencies.isLoaded(mobType.getModID())) {
                registeredItems.add(new ItemDataModel(mobType));
            }
        }
    }
}
