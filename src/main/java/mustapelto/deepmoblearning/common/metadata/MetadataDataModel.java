package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.DMLRegistry;
import mustapelto.deepmoblearning.common.util.ItemStackDefinitionHelper;
import mustapelto.deepmoblearning.common.util.JsonHelper;
import mustapelto.deepmoblearning.common.util.StringHelper;
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
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Created by mustapelto on 2021-02-14
 */
public class MetadataDataModel extends Metadata {
    public static final MetadataDataModel INVALID = new MetadataDataModel();

    // Data from JSON
    private final String displayName; // Used in Data Model and Pristine Matter display name, and Deep Learner GUI. Default: metadataID
    private final String displayNamePlural; // Plural form of display name. Used in Deep Learner GUI. Default: displayName + "s"
    private final String livingMatterString; // Living Matter type gained from simulating this Model.
    private final int simulationRFCost; // Cost to simulate this Model in RF/t. Default: 256
    private final String extraTooltip; // Extra tooltip to display on Data Model item. Default: ""
    private final ImmutableList<String> craftingIngredientStrings; // Ingredients to craft this model (in addition to a Blank Model). Default: none
    private final ImmutableList<String> associatedMobs; // List of mobs that increase Model data. Default: ["modID:metadataID"]
    private final ImmutableList<String> lootItemStrings; // List of possible loot items from this Model's Pristine Matter. Default: ["minecraft:stone"]
    private final ImmutableList<String> trialRewardStrings; // List of rewards for a Trial with this mob. Default: []
    private final DeepLearnerDisplayData deepLearnerDisplayData; // Data used in Deep Learner display

    // Calculated data
    private final ResourceLocation dataModelRegistryName; // deepmoblearning:data_model_[metadataID]
    private final ResourceLocation pristineMatterRegistryName; // deepmoblearning:pristine_matter_[metadataID]

    private ImmutableList<ItemStack> lootItems; // List of actual ItemStacks that can be selected as "loot"
    private ImmutableList<ItemStack> trialRewards; // List of actual ItemStacks that are received as trial reward
    private IRecipe craftingRecipe; // Recipe to craft this Data Model
    private ItemStack livingMatter; // Living Matter data associated with this Data Model
    private ItemStack pristineMatter; // Pristine Matter associated with this Data Model

    private MetadataDataModel() {
        super("", "");
        displayName = "INVALID";
        displayNamePlural = "INVALID";
        livingMatterString = "";
        simulationRFCost = Integer.MAX_VALUE;
        extraTooltip = "";
        craftingIngredientStrings = ImmutableList.of();
        associatedMobs = ImmutableList.of();
        lootItemStrings = ImmutableList.of();
        trialRewardStrings = ImmutableList.of();
        deepLearnerDisplayData = DeepLearnerDisplayData.INVALID;
        dataModelRegistryName = new ResourceLocation("");
        pristineMatterRegistryName = new ResourceLocation("");
    }

    public MetadataDataModel(JsonObject data, String categoryID, String metadataID) {
        super(categoryID, metadataID);

        displayName = JsonHelper.getString(data, "displayName", StringHelper.uppercaseFirst(metadataID));
        displayNamePlural = JsonHelper.getString(data, "displayNamePlural", displayName + "s");
        livingMatterString = JsonHelper.getString(data, "livingMatter");
        simulationRFCost = JsonHelper.getInt(data, "simulationRFCost", 256, 0, DMLConstants.SimulationChamber.ENERGY_IN_MAX);
        extraTooltip = JsonHelper.getString(data, "extraTooltip");
        craftingIngredientStrings = JsonHelper.getStringListFromJsonArray(data, "craftingIngredients");
        associatedMobs = JsonHelper.getStringListFromJsonArray(data, "associatedMobs", String.format("%s:%s", categoryID, metadataID));
        lootItemStrings = JsonHelper.getStringListFromJsonArray(data, "lootItems", "minecraft:stone");
        trialRewardStrings = JsonHelper.getStringListFromJsonArray(data, "trialRewards");

        JsonObject deepLearnerDisplayJSON = JsonHelper.getJsonObject(data, "deepLearnerDisplay");
        deepLearnerDisplayData = new DeepLearnerDisplayData(deepLearnerDisplayJSON, categoryID, metadataID);

        dataModelRegistryName = new ResourceLocation(DMLConstants.ModInfo.ID, "data_model_" + metadataID);
        pristineMatterRegistryName = new ResourceLocation(DMLConstants.ModInfo.ID, "pristine_matter_" + metadataID);
    }

    @Override
    public void finalizeData() {
        // Replace placeholder strings with actual item name and build ItemStack lists
        ImmutableList<String> replacedLootList = StringHelper.replaceInList(lootItemStrings, DMLConstants.Recipes.Placeholders.DATA_MODEL, dataModelRegistryName.toString());
        replacedLootList = StringHelper.replaceInList(replacedLootList, DMLConstants.Recipes.Placeholders.PRISTINE_MATTER, pristineMatterRegistryName.toString());
        lootItems = ItemStackDefinitionHelper.itemStackListFromStringList(replacedLootList);

        ImmutableList<String> replacedTrialList = StringHelper.replaceInList(trialRewardStrings, DMLConstants.Recipes.Placeholders.DATA_MODEL, dataModelRegistryName.toString());
        replacedTrialList = StringHelper.replaceInList(replacedTrialList, DMLConstants.Recipes.Placeholders.PRISTINE_MATTER, pristineMatterRegistryName.toString());
        trialRewards = ItemStackDefinitionHelper.itemStackListFromStringList(replacedTrialList);

        // Build crafting recipe
        ImmutableList<String> replacedIngredientList = StringHelper.replaceInList(craftingIngredientStrings, DMLConstants.Recipes.Placeholders.DATA_MODEL, dataModelRegistryName.toString());
        replacedIngredientList = StringHelper.replaceInList(replacedIngredientList, DMLConstants.Recipes.Placeholders.PRISTINE_MATTER, pristineMatterRegistryName.toString());
        buildCraftingRecipe(replacedIngredientList);

        // Get associated Living Matter
        livingMatter = DMLRegistry.getLivingMatter(livingMatterString);
        pristineMatter = DMLRegistry.getPristineMatter(pristineMatterRegistryName.getResourcePath());
    }

    private void buildCraftingRecipe(ImmutableList<String> ingredientStringList) {
        ItemStack output = DMLRegistry.getDataModel(getMetadataID());
        if (output.isEmpty())
            return;

        NonNullList<Ingredient> ingredients = NonNullList.create();
        Ingredient blankModel = CraftingHelper.getIngredient(new ItemStack(DMLRegistry.ITEM_DATA_MODEL_BLANK));
        ingredients.add(blankModel);

        boolean isOreRecipe = false;
        ImmutableList<Ingredient> customIngredients = ItemStackDefinitionHelper.ingredientListFromStringList(ingredientStringList);
        for (Ingredient ingredient : customIngredients) {
            if (ingredient.equals(Ingredient.EMPTY)) {
                DMLRelearned.logger.warn("Invalid Data Model crafting ingredient. Skipping.");
                continue;
            }
            if (ingredient instanceof OreIngredient)
                isOreRecipe = true;

            ingredients.add(ingredient);
        }

        if (isOreRecipe)
            craftingRecipe = new ShapelessOreRecipe(DMLConstants.Recipes.Groups.DATA_MODELS, output, ingredients);
        else
            craftingRecipe = new ShapelessRecipes(DMLConstants.Recipes.Groups.DATA_MODELS.toString(), output, ingredients);

        craftingRecipe.setRegistryName(dataModelRegistryName);
    }

    @Override
    public boolean isInvalid() {
        return this.equals(INVALID);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNamePlural() {
        return displayNamePlural;
    }

    public int getSimulationRFCost() {
        return simulationRFCost;
    }

    public ItemStack getLivingMatter(int size) {
        ItemStack result = livingMatter.copy();
        result.setCount(size);
        return result;
    }

    public ItemStack getLivingMatter() {
        return getLivingMatter(1);
    }

    public ItemStack getPristineMatter(int size) {
        ItemStack result = pristineMatter.copy();
        result.setCount(size);
        return result;
    }

    public ItemStack getPristineMatter() {
        return getPristineMatter(1);
    }

    public String getExtraTooltip() {
        return extraTooltip;
    }

    public DeepLearnerDisplayData getDeepLearnerDisplayData() {
        return deepLearnerDisplayData;
    }

    public ResourceLocation getDataModelRegistryName() {
        return dataModelRegistryName;
    }

    public ResourceLocation getPristineMatterRegistryName() {
        return pristineMatterRegistryName;
    }

    public ResourceLocation getDataModelTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + dataModelRegistryName.getResourcePath() + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + dataModelRegistryName.getResourcePath());
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Data Model texture for {} not found. Using default texture.", getMetadataID());
            return DMLConstants.DefaultModels.DATA_MODEL;
        }
    }

    public ResourceLocation getPristineMatterTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + pristineMatterRegistryName.getResourcePath() + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + pristineMatterRegistryName.getResourcePath());
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Pristine Matter texture for {} not found. Using default texture.", getMetadataID());
            return DMLConstants.DefaultModels.PRISTINE_MATTER;
        }
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

    @Nonnull
    public ImmutableList<ItemStack> getLootItems() {
        if (lootItems == null)
            return ImmutableList.of();
        return lootItems;
    }

    public ItemStack getLootItem(int index) {
        if (index >= 0 && index < lootItems.size())
            return lootItems.get(index).copy();

        return ItemStack.EMPTY;
    }

    public ImmutableList<ItemStack> getTrialRewards() {
        if (trialRewards == null)
            return ImmutableList.of();
        return trialRewards;
    }

    public ItemStack getTrialRewardItem(int index) {
        if (index >= 0 && index < trialRewards.size())
            return trialRewards.get(index).copy();

        return ItemStack.EMPTY;
    }

    public IRecipe getCraftingRecipe() {
        return craftingRecipe;
    }

    public static class DeepLearnerDisplayData {
        public static final DeepLearnerDisplayData INVALID = new DeepLearnerDisplayData();

        private final int hearts; // Number of hearts. 0 will show as obfuscated text. Default: 10
        private final ImmutableList<String> mobTrivia; // Mob trivia text. Default: "Nothing is known about this mob."
        private final String entityName; // Registry name of displayed entity. Default: "modID:metadataID"
        private final String entityHeldItem; // Registry name of item held by displayed entity. Default: ""
        private final int entityScale; // Scale of displayed entity. Default: 40
        private final int entityOffsetX; // X offset of displayed entity. Default: 0
        private final int entityOffsetY; // Y offset of displayed entity. Default: 0
        private final String extraEntityName; // Registry name of additional displayed entity. Default: ""
        private final boolean extraEntityIsChild; // Is additional displayed entity a child (e.g. zombie)? Default: false
        private final int extraEntityOffsetX; // X offset of additional entity. Default: 0
        private final int extraEntityOffsetY; // Y offset of additional entity. Default: 0

        private DeepLearnerDisplayData() {
            hearts = 0;
            mobTrivia = ImmutableList.of("INVALID");
            entityName = "";
            entityHeldItem = "";
            entityScale = 1;
            entityOffsetX = 0;
            entityOffsetY = 0;
            extraEntityName = "";
            extraEntityIsChild = false;
            extraEntityOffsetX = 0;
            extraEntityOffsetY = 0;
        }

        public DeepLearnerDisplayData(JsonObject data, String categoryID, String metadataID) {
            hearts = JsonHelper.getInt(data, "hearts", 0, 0, Integer.MAX_VALUE);
            mobTrivia = JsonHelper.getStringListFromJsonArray(data, "mobTrivia", "Nothing is known about this mob.");

            String defaultEntityName = String.format("%s:%s", categoryID, metadataID);
            entityName = JsonHelper.getRegistryName(data, "entity", defaultEntityName);

            entityHeldItem = JsonHelper.getRegistryName(data, "entityHeldItem");
            entityScale = JsonHelper.getInt(data, "entityScale", 40, 0, 200);
            entityOffsetX = JsonHelper.getInt(data, "entityOffsetX", 0, -200, 200);
            entityOffsetY = JsonHelper.getInt(data, "entityOffsetY", 0, -200, 200);
            extraEntityName = JsonHelper.getRegistryName(data, "extraEntity");
            extraEntityIsChild = JsonHelper.getBoolean(data, "extraEntityIsChild");
            extraEntityOffsetX = JsonHelper.getInt(data, "extraEntityOffsetX", 0, -200, 200);
            extraEntityOffsetY = JsonHelper.getInt(data, "extraEntityOffsetY", 0, -200, 200);
        }

        public int getHearts() {
            return hearts;
        }

        public ImmutableList<String> getMobTrivia() {
            return mobTrivia;
        }

        public Entity getEntity(World world) {
            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(entityName), world);
            if (!entityHeldItem.isEmpty() && entity instanceof EntityLiving) {
                Item heldItem = Item.getByNameOrId(entityHeldItem);
                if (heldItem != null)
                    ((EntityLiving) entity).setHeldItem(EnumHand.MAIN_HAND, new ItemStack(heldItem));
            }

            return entity;
        }

        public int getEntityScale() {
            return entityScale;
        }

        public int getEntityOffsetX() {
            return entityOffsetX;
        }

        public int getEntityOffsetY() {
            return entityOffsetY;
        }

        public Entity getExtraEntity(World world) {
            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(extraEntityName), world);
            if (extraEntityIsChild) {
                if (entity instanceof EntityZombie) {
                    ((EntityZombie) entity).setChild(true);
                } else if (entity instanceof EntityAgeable) {
                    ((EntityAgeable) entity).setGrowingAge(-1000000); // big number -> won't grow up while Deep Learner is open
                }
            }
            return entity;
        }

        public boolean isExtraEntityIsChild() {
            return extraEntityIsChild;
        }

        public int getExtraEntityOffsetX() {
            return extraEntityOffsetX;
        }

        public int getExtraEntityOffsetY() {
            return extraEntityOffsetY;
        }
    }
}
