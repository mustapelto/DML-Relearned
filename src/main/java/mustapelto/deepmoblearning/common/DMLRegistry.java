package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.blocks.*;
import mustapelto.deepmoblearning.common.entities.EntityItemGlitchFragment;
import mustapelto.deepmoblearning.common.items.*;
import mustapelto.deepmoblearning.common.metadata.LivingMatterDataManager;
import mustapelto.deepmoblearning.common.metadata.MobMetadataManager;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class DMLRegistry {
    public static final NonNullList<Item> registeredItems = NonNullList.create();
    public static final NonNullList<BlockBase> registeredBlocks = NonNullList.create();

    // Dynamic items, referenced by ID
    public static final Map<String, ItemLivingMatter> registeredLivingMatter = new HashMap<>();
    public static final Map<String, ItemDataModel> registeredDataModels = new HashMap<>();
    public static final Map<String, ItemPristineMatter> registeredPristineMatter = new HashMap<>();

    // Blocks
    public static final BlockInfusedIngot BLOCK_INFUSED_INGOT = new BlockInfusedIngot();
    public static final BlockMachineCasing BLOCK_MACHINE_CASING = new BlockMachineCasing();
    public static final BlockSimulationChamber BLOCK_SIMULATION_CHAMBER = new BlockSimulationChamber();
    public static final BlockLootFabricator BLOCK_LOOT_FABRICATOR = new BlockLootFabricator();

    // Items
    public static final ItemDeepLearner ITEM_DEEP_LEARNER = new ItemDeepLearner();
    public static final ItemPolymerClay ITEM_POLYMER_CLAY = new ItemPolymerClay();
    public static final ItemDataModelBlank ITEM_DATA_MODEL_BLANK = new ItemDataModelBlank();
    public static final ItemCreativeModelLearner ITEM_CREATIVE_MODEL_LEARNER = new ItemCreativeModelLearner();
    public static final ItemSootedRedstone ITEM_SOOTED_REDSTONE = new ItemSootedRedstone();
    public static final ItemSootedPlate ITEM_SOOTED_PLATE = new ItemSootedPlate();
    public static final ItemGlitchIngot ITEM_GLITCH_INGOT = new ItemGlitchIngot();
    public static final ItemGlitchFragment ITEM_GLITCH_FRAGMENT = new ItemGlitchFragment();
    public static final ItemGlitchHeart ITEM_GLITCH_HEART = new ItemGlitchHeart();

    // Armor and Weapons
    public static final ItemGlitchArmor.ItemGlitchHelmet ITEM_GLITCH_HELMET = new ItemGlitchArmor.ItemGlitchHelmet();
    public static final ItemGlitchArmor.ItemGlitchChestplate ITEM_GLITCH_CHESTPLATE = new ItemGlitchArmor.ItemGlitchChestplate();
    public static final ItemGlitchArmor.ItemGlitchLeggings ITEM_GLITCH_LEGGINGS = new ItemGlitchArmor.ItemGlitchLeggings();
    public static final ItemGlitchArmor.ItemGlitchBoots ITEM_GLITCH_BOOTS = new ItemGlitchArmor.ItemGlitchBoots();
    public static final ItemGlitchSword ITEM_GLITCH_SWORD = new ItemGlitchSword();

    // Entity ID
    private static int entityId = 0;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        registeredBlocks.add(BLOCK_INFUSED_INGOT);
        registeredBlocks.add(BLOCK_MACHINE_CASING);
        registeredBlocks.add(BLOCK_SIMULATION_CHAMBER);
        registeredBlocks.add(BLOCK_LOOT_FABRICATOR);

        IForgeRegistry<Block> registry = event.getRegistry();
        registeredBlocks.forEach(registry::register);

        // Register tile entities
        GameRegistry.registerTileEntity(BLOCK_SIMULATION_CHAMBER.getTileEntityClass(),new ResourceLocation(DMLConstants.ModInfo.ID, "simulation_chamber"));
        GameRegistry.registerTileEntity(BLOCK_LOOT_FABRICATOR.getTileEntityClass(), new ResourceLocation(DMLConstants.ModInfo.ID, "extraction_chamber"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        DMLRelearned.logger.info("Registering Items...");
        // Misc Items
        registeredItems.add(ITEM_POLYMER_CLAY);
        registeredItems.add(ITEM_DATA_MODEL_BLANK);
        registeredItems.add(ITEM_DEEP_LEARNER);
        registeredItems.add(ITEM_CREATIVE_MODEL_LEARNER);
        registeredItems.add(ITEM_SOOTED_REDSTONE);
        registeredItems.add(ITEM_SOOTED_PLATE);
        registeredItems.add(ITEM_GLITCH_INGOT);
        registeredItems.add(ITEM_GLITCH_FRAGMENT);
        registeredItems.add(ITEM_GLITCH_HEART);

        // Glitch Armor and Sword
        registeredItems.add(ITEM_GLITCH_HELMET);
        registeredItems.add(ITEM_GLITCH_CHESTPLATE);
        registeredItems.add(ITEM_GLITCH_LEGGINGS);
        registeredItems.add(ITEM_GLITCH_BOOTS);
        registeredItems.add(ITEM_GLITCH_SWORD);

        DMLRelearned.logger.info("Registering Living Matter...");
        LivingMatterDataManager.getDataStore().forEach((key, value) -> registeredLivingMatter.put(key, new ItemLivingMatter(value)));
        registeredItems.addAll(registeredLivingMatter.values());

        DMLRelearned.logger.info("Registering Data Models and Pristine Matter...");
        MobMetadataManager.getDataStore().forEach((key, value) -> {
            registeredDataModels.put(key, new ItemDataModel(value));
            registeredPristineMatter.put(key, new ItemPristineMatter(value));
        });

        registeredItems.addAll(registeredDataModels.values());
        registeredItems.addAll(registeredPristineMatter.values());


        IForgeRegistry<Item> registry = event.getRegistry();
        registeredItems.forEach(registry::register);

        // Register ItemBlocks
        registeredBlocks.forEach(block -> registry.register(block.getItemBlock()));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();

        final ResourceLocation itemGlitchFragmentRegistryName = new ResourceLocation(DMLConstants.ModInfo.ID, "item_glitch_fragment");

        EntityEntry itemGlitchFragment = EntityEntryBuilder.create()
                .entity(EntityItemGlitchFragment.class)
                .id(itemGlitchFragmentRegistryName, entityId++)
                .name(itemGlitchFragmentRegistryName.getResourcePath())
                .tracker(64, 1, true)
                .build();

        registry.registerAll(itemGlitchFragment);
    }
}
