package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelDataModel;
import mustapelto.deepmoblearning.client.models.ModelPristineMatter;
import mustapelto.deepmoblearning.common.DMLRegistry;
import mustapelto.deepmoblearning.common.util.ItemStringBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static mustapelto.deepmoblearning.common.util.JsonHelper.getJsonArrayAsStringList;
import static mustapelto.deepmoblearning.common.util.JsonHelper.getOrDefault;

public class MobMetadata {
    private final String modID; // Mod ID the item is related to. "minecraft" for vanilla-related items.
    private final String itemID; // Used for item registry and texture loading. No default value, item won't load if itemID is not set
    private final String displayName; // Used on Data Model and Pristine Matter items and in Deep Learner GUI. Default: itemID with first letter capitalized
    private final String displayNamePlural; // Plural form of display name. Used in Deep Learner GUI. Default: displayName + "s"
    private final LivingMatterData livingMatter; // Associated Living Matter type. Default: first available
    private final int simulationRFCost; // Cost to simulate this mob in RF/t. Default: 256
    private final ImmutableList<String> craftingIngredients; // Crafting recipe for this Data Model
    private final String extraTooltip; // Extra tooltip to display on Data Model item. Default: ""
    private final ImmutableList<String> associatedMobs; // List of mobs that will increase model data. Format: "modid:mobname" (e.g. "minecraft:blaze"). Default: modID:itemID
    private final ImmutableList<ItemStack> lootItemList; // List of available loot items (produced from Pristine Matter in Loot Fabricator). Format: "modid:itemid" (e.g. "minecraft:stone").
    private final ImmutableList<ItemStack> trialRewardItemList; // List of configured trial reward items for this mob (built once at init)
    private final int numberOfHearts; // Number of hearts to show in Deep Learner GUI. Default: 10
    private final ImmutableList<String> mobTrivia; // Trivia text to show in Deep Learner GUI. Default: "Nothing is known about this mob."
    private final String displayEntityID; // ID of entity to display in Deep Learner GUI. Default: modID:itemID
    private final String displayEntityHeldItem; // Item held by entity in Deep Learner GUI. Default: ""
    private final int displayEntityScale; // Scale of entity in Deep Learner GUI. Default: 40
    private final int displayEntityOffsetX; // X offset of entity in Deep Learner GUI. Default: 0
    private final int displayEntityOffsetY; // Y offset of entity in Deep Learner GUI. Default: 0
    private final String displayExtraEntityID; // ID of additional entity to display in Deep Learner GUI. Default: ""
    private final boolean displayExtraEntityIsChild; // Is additional entity a child form (e.g. zombie)? Default: false
    private final int displayExtraEntityOffsetX; // X offset of additional entity in Deep Learner GUI. Default: 0
    private final int displayExtraEntityOffsetY; // Y offset of additional entity in Deep Learner GUI. Default: 0

    private final String modelName; // Name of data model item = "data_model_" + itemID
    private final String pristineName; // Name of pristine matter item = "pristine_matter_" + itemID

    private MobMetadata(String modID, JsonObject data) {
        if (!data.has("itemID")) {
            throw new IllegalArgumentException("Item ID missing on Data Model entry. Cannot create items.");
        }

        // TODO: move some stuff from preinit to init (loot items, recipes etc.) - same for other types of metadata!!!

        itemID = data.get("itemID").getAsString();
        modelName = "data_model_" + itemID; // Name of associated data model item
        pristineName = "pristine_matter_" + itemID; // Name of associated pristine matter item
        this.modID = modID;
        displayName = getOrDefault(data, "displayName", itemID.substring(0, 1).toUpperCase() + itemID.substring(1));
        displayNamePlural = getOrDefault(data, "displayNamePlural", "");
        String livingMatterString = getOrDefault(data, "livingMatter", "");
        livingMatter = LivingMatterDataManager.getByID(livingMatterString);
        simulationRFCost = getOrDefault(data, "simulationRFCost", 256, 0, DMLConstants.SimulationChamber.ENERGY_IN_MAX);
        craftingIngredients = getJsonArrayAsStringList(data, "craftingIngredients");
        extraTooltip = getOrDefault(data, "extraTooltip", "");
        String defaultEntityString = String.format("%s:%s", modID, itemID); // Used if entry for associatedMobs or deepLearnerDisplay.entityID is empty
        associatedMobs = getJsonArrayAsStringList(data, "associatedMobs", defaultEntityString);
        lootItemList = ItemStringBuilder.itemStackListFromStringList(getJsonArrayAsStringList(data, "lootItems"));
        trialRewardItemList = ItemStringBuilder.itemStackListFromStringList(getJsonArrayAsStringList(data, "trialRewards"));

        // Deep Learner Display Settings
        JsonObject displaySettings = data.get("deepLearnerDisplay").getAsJsonObject();
        numberOfHearts = getOrDefault(displaySettings, "numberOfHearts", 0, 0, Integer.MAX_VALUE);
        mobTrivia = getJsonArrayAsStringList(displaySettings, "mobTrivia", "Nothing is known about this mob.");
        displayEntityID = getOrDefault(displaySettings, "entityID", String.format("%s:%s", modID, itemID));
        displayEntityHeldItem = getOrDefault(displaySettings, "entityHeldItem", "");
        displayEntityScale = getOrDefault(displaySettings, "entityScale", 40, 0, 200);
        displayEntityOffsetX = getOrDefault(displaySettings, "entityOffsetX", 0, -200, 200);
        displayEntityOffsetY = getOrDefault(displaySettings, "entityOffsetY", 0, -200, 200);
        displayExtraEntityID = getOrDefault(displaySettings, "extraEntityID", "");
        displayExtraEntityIsChild = getOrDefault(displaySettings, "extraEntityIsChild", false);
        displayExtraEntityOffsetX = getOrDefault(displaySettings, "extraEntityOffsetX", 0, -200, 200);
        displayExtraEntityOffsetY = getOrDefault(displaySettings, "extraEntityOffsetY", 0, -200, 200);
    }

    public static MobMetadata deserialize(String modID, JsonObject data) {
        try {
            return new MobMetadata(modID, data);
        } catch (IllegalArgumentException e) {
            DMLRelearned.logger.warn(e.getMessage());
            return null;
        }
    }

    // Field Getters and Helpers
    public boolean isModLoaded() {
        return modID.equals(DMLConstants.MINECRAFT) || Loader.isModLoaded(modID);
    }

    public String getItemID() {
        return itemID;
    }

    public String getDataModelName() {
        return "data_model_" + itemID;
    }

    public String getPristineMatterName() {
        return "pristine_matter_" + itemID;
    }

    public ResourceLocation getDataModelTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + modelName + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + modelName);
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Data Model texture for {} not found. Using default texture.", itemID);
            return ModelDataModel.DEFAULT_LOCATION;
        }
    }

    public ResourceLocation getPristineMatterTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + pristineName + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + pristineName);
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Pristine Matter texture for {} not found. Using default texture.", itemID);
            return ModelPristineMatter.DEFAULT_LOCATION;
        }
    }

    @Nonnull
    public LivingMatterData getLivingMatterData() {
        return livingMatter;
    }

    @Nonnull
    public ItemStack getPristineMatter() {
        return getPristineMatter(1);
    }

    @Nonnull
    public ItemStack getPristineMatter(int size) {
        return new ItemStack(DMLRegistry.registeredPristineMatter.get(itemID), size);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNamePlural() {
        return !displayNamePlural.isEmpty() ? displayNamePlural : getDisplayName() + "s";
    }

    public int getSimulationRFCost() {
        return simulationRFCost;
    }

    public IRecipe getCraftingRecipe() {
        Item output = DMLRegistry.registeredDataModels.get(itemID);
        if (output == null)
            return null;

        List<Ingredient> ingredientList = new ArrayList<>();
        Ingredient blankModel = CraftingHelper.getIngredient(new ItemStack(DMLRegistry.ITEM_DATA_MODEL_BLANK));
        ingredientList.add(blankModel);

        boolean isOreRecipe = false;
        ImmutableList<Ingredient> customIngredientsList = ItemStringBuilder.ingredientListFromStringList(craftingIngredients);
        for (Ingredient ingredient : customIngredientsList) {
            if (ingredient.equals(Ingredient.EMPTY)) {
                DMLRelearned.logger.warn("Invalid Data Model crafting recipe. Either an entry is invalid or recipe contains items from a mod that is not loaded.");
                return null;
            }
            if (ingredient instanceof OreIngredient)
                isOreRecipe = true;
            ingredientList.add(ingredient);
        }

        ResourceLocation group = new ResourceLocation(DMLConstants.ModInfo.ID, "data_models");

        IRecipe recipe;

        if (isOreRecipe)
            recipe = new ShapelessOreRecipe(group, output, ingredientList);
        else {
            NonNullList<Ingredient> input = NonNullList.create();
            input.addAll(ingredientList);
            recipe = new ShapelessRecipes(group.toString(), new ItemStack(output), input);
        }

        recipe.setRegistryName(new ResourceLocation(DMLConstants.ModInfo.ID, getDataModelName()));
        return recipe;
    }

    public String getExtraTooltip() {
        return extraTooltip;
    }

    public int getNumberOfHearts() {
        return numberOfHearts;
    }

    public ImmutableList<String> getMobTrivia() {
        return mobTrivia;
    }

    public Entity getEntity(World world) {
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(displayEntityID), world);
        if (!displayEntityHeldItem.isEmpty() && entity instanceof EntityLiving) {
            Item heldItem = Item.getByNameOrId(displayEntityHeldItem);
            if (heldItem != null)
                ((EntityLiving) entity).setHeldItem(EnumHand.MAIN_HAND, new ItemStack(heldItem));
        }

        return entity;
    }

    public int getDisplayEntityScale() {
        return displayEntityScale;
    }

    public int getDisplayEntityOffsetX() {
        return displayEntityOffsetX;
    }

    public int getDisplayEntityOffsetY() {
        return displayEntityOffsetY;
    }

    public Entity getExtraEntity(World world) {
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(displayExtraEntityID), world);
        if (displayExtraEntityIsChild) {
            if (entity instanceof EntityZombie) {
                ((EntityZombie) entity).setChild(true);
            } else if (entity instanceof EntityAgeable) {
                ((EntityAgeable) entity).setGrowingAge(-1000000); // big number -> won't grow up while Deep Learner is open
            }
        }
        return entity;
    }

    public int getDisplayExtraEntityOffsetX() {
        return displayExtraEntityOffsetX;
    }

    public int getDisplayExtraEntityOffsetY() {
        return displayExtraEntityOffsetY;
    }

    public boolean isAssociatedMob(EntityLivingBase entity) {
        EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
        if (entityEntry == null)
            return false;

        ResourceLocation registryName = entityEntry.getRegistryName();
        if (registryName == null)
            return false;

        String name = registryName.toString();
        return associatedMobs.contains(name);
    }

    public ImmutableList<ItemStack> getLootItemList() {
        return lootItemList;
    }

    public ItemStack getLootItem(int index) {
        if (index >= 0 && index < lootItemList.size())
            return lootItemList.get(index).copy();

        return ItemStack.EMPTY;
    }

    public ImmutableList<ItemStack> getTrialRewardItemList() {
        return trialRewardItemList;
    }

    public ItemStack getTrialRewardItem(int index) {
        if (index >= 0 && index < trialRewardItemList.size())
            return trialRewardItemList.get(index).copy();

        return ItemStack.EMPTY;
    }
}