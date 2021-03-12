package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.FileHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

import java.io.File;

/**
 * Created by mustapelto on 2021-02-14
 */
public class MetadataManagerLivingMatter extends MetadataManager<MetadataLivingMatter> {
    public static MetadataManagerLivingMatter INSTANCE = new MetadataManagerLivingMatter();

    private ImmutableList<JsonObject> craftingRecipesJson;

    private MetadataManagerLivingMatter() {
        super("LivingMatter.json");
        readCraftingRecipeFile();
    }

    private void readCraftingRecipeFile() {
        final String filename = "LivingMatterRecipes.json";
        File livingMatterRecipeFile = new File(FileHelper.configDML, filename);
        if (!livingMatterRecipeFile.exists())
            FileHelper.copyFromJar("/settings/" + filename, livingMatterRecipeFile.toPath());

        FileHelper.readArray(livingMatterRecipeFile).ifPresent(this::parseCraftingRecipes);
    }

    private void parseCraftingRecipes(JsonArray array) {
        ImmutableList.Builder<JsonObject> builder = ImmutableList.builder();

        array.forEach(item -> {
            if (item.isJsonObject())
                builder.add(item.getAsJsonObject());
            else {
                DMLRelearned.logger.warn("Invalid entry in JSON array: not an object! Skipping.");
            }
        });

        craftingRecipesJson = builder.build();
    }

    @Override
    public void loadData() {
        DMLRelearned.logger.info("Loading Living Matter data from JSON...");
        super.loadData();
    }

    @Override
    public void finalizeData() {
        DMLRelearned.logger.info("Finalizing Living Matter data...");
        super.finalizeData();
    }

    @Override
    protected MetadataLivingMatter constructMetadataFromJson(JsonObject data, String categoryName, String entryName) {
        return new MetadataLivingMatter(data, categoryName, entryName);
    }

    public ImmutableMap<String, ResourceLocation> getLivingMatterTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataStore.forEach((k, v) -> {
            ResourceLocation livingMatterTexture = v.getLivingMatterTexture();
            if (!livingMatterTexture.equals(DMLConstants.DefaultModels.LIVING_MATTER))
                builder.put(k, livingMatterTexture);
        });

        return builder.build();
    }

    public ImmutableList<IRecipe> getCraftingRecipes() {
        ImmutableList.Builder<IRecipe> builder = ImmutableList.builder();
        JsonContext ctx = new JsonContext(DMLConstants.ModInfo.ID);

        int recipeCount = 0;
        for(JsonObject entry : craftingRecipesJson) {
            IRecipe recipe;
            try {
                recipe = CraftingHelper.getRecipe(entry, ctx);
            } catch (Exception e) {
                DMLRelearned.logger.warn("Error reading Living Matter recipe entry! Error message: {}", e.getMessage());
                continue;
            }
            recipe.setRegistryName(new ResourceLocation("deepmoblearning:living_matter_conversion_" + recipeCount));
            builder.add(recipe);
            recipeCount++;
        }

        return builder.build();
    }

}
