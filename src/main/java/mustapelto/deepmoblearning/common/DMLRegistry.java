package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.blocks.BlockInfusedIngot;
import mustapelto.deepmoblearning.common.blocks.BlockMachineCasing;
import mustapelto.deepmoblearning.common.items.*;
import mustapelto.deepmoblearning.common.metadata.LivingMatterDataManager;
import mustapelto.deepmoblearning.common.metadata.MobMetaDataManager;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@EventBusSubscriber
public class DMLRegistry {
    public static final NonNullList<Item> registeredItems = NonNullList.create();
    public static final NonNullList<Block> registeredBlocks = NonNullList.create();

    // Dynamic items, referenced by ID
    public static final Map<String, ItemLivingMatter> registeredLivingMatter = new HashMap<>();
    public static final Map<String, ItemDataModel> registeredDataModels = new HashMap<>();
    public static final Map<String, ItemPristineMatter> registeredPristineMatter = new HashMap<>();

    // Blocks
    public static final BlockInfusedIngot blockInfusedIngot = new BlockInfusedIngot();
    public static final BlockMachineCasing blockMachineCasing = new BlockMachineCasing();

    // Items
    public static final ItemDeepLearner itemDeepLearner = new ItemDeepLearner();
    public static final ItemPolymerClay itemPolymerClay = new ItemPolymerClay();
    public static final ItemDataModelBlank itemDataModelBlank = new ItemDataModelBlank();
    public static final ItemCreativeModelLearner itemCreativeModelLearner = new ItemCreativeModelLearner();
    public static final ItemSootedRedstone itemSootedRedstone = new ItemSootedRedstone();
    public static final ItemSootedPlate itemSootedPlate = new ItemSootedPlate();
    public static final ItemGlitchIngot itemGlitchIngot = new ItemGlitchIngot();
    public static final ItemGlitchFragment itemGlitchFragment = new ItemGlitchFragment();
    public static final ItemGlitchHeart itemGlitchHeart = new ItemGlitchHeart();

    // Armor and Weapons
    public static final ItemGlitchArmor.ItemGlitchHelmet itemGlitchHelmet = new ItemGlitchArmor.ItemGlitchHelmet();
    public static final ItemGlitchArmor.ItemGlitchChestplate itemGlitchChestplate = new ItemGlitchArmor.ItemGlitchChestplate();
    public static final ItemGlitchArmor.ItemGlitchLeggings itemGlitchLeggings = new ItemGlitchArmor.ItemGlitchLeggings();
    public static final ItemGlitchArmor.ItemGlitchBoots itemGlitchBoots = new ItemGlitchArmor.ItemGlitchBoots();
    public static final ItemGlitchSword itemGlitchSword = new ItemGlitchSword();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        registeredBlocks.add(blockInfusedIngot);
        registeredBlocks.add(blockMachineCasing);

        IForgeRegistry<Block> registry = event.getRegistry();
        registeredBlocks.forEach(registry::register);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        DMLRelearned.logger.info("Registering Items...");
        // Misc Items
        registeredItems.add(itemPolymerClay);
        registeredItems.add(itemDataModelBlank);
        registeredItems.add(itemDeepLearner);
        registeredItems.add(itemCreativeModelLearner);
        registeredItems.add(itemSootedRedstone);
        registeredItems.add(itemSootedPlate);
        registeredItems.add(itemGlitchIngot);
        registeredItems.add(itemGlitchFragment);
        registeredItems.add(itemGlitchHeart);

        // Glitch Armor and Sword
        registeredItems.add(itemGlitchHelmet);
        registeredItems.add(itemGlitchChestplate);
        registeredItems.add(itemGlitchLeggings);
        registeredItems.add(itemGlitchBoots);
        registeredItems.add(itemGlitchSword);

        DMLRelearned.logger.info("Registering Living Matter...");
        LivingMatterDataManager.getDataStore().forEach((key, value) -> registeredLivingMatter.put(key, new ItemLivingMatter(value)));
        registeredItems.addAll(registeredLivingMatter.values());

        DMLRelearned.logger.info("Registering Data Models and Pristine Matter...");
        MobMetaDataManager.getDataStore().forEach((key, value) -> {
            registeredDataModels.put(key, new ItemDataModel(value));
            registeredPristineMatter.put(key, new ItemPristineMatter(value));
        });

        registeredItems.addAll(registeredDataModels.values());
        registeredItems.addAll(registeredPristineMatter.values());


        IForgeRegistry<Item> registry = event.getRegistry();
        registeredItems.forEach(registry::register);

        // Register ItemBlocks
        registeredBlocks.forEach(block -> {
            registry.register(new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
        });
    }
}
