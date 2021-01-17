package mustapelto.deepmoblearning.common.registry;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.items.*;
import mustapelto.deepmoblearning.common.metadata.LivingMatterDataManager;
import mustapelto.deepmoblearning.common.metadata.MobMetaDataManager;
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
        registeredItems.add(new ItemCreativeModelLearner());

        DMLRelearned.logger.info("Registering Living Matter...");
        LivingMatterDataManager.getDataStore().forEach((key, value) -> registeredItems.add(new ItemLivingMatter(value)));
        DMLRelearned.logger.info("Registering Data Models...");
        MobMetaDataManager.getDataStore().forEach((key, value) -> registeredItems.add(new ItemDataModel(value)));

        IForgeRegistry<Item> registry = event.getRegistry();
        registeredItems.forEach(registry::register);
    }
}
