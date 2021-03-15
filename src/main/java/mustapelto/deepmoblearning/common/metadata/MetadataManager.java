package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.DMLRHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class MetadataManager {
    private static final String INTERNAL_PATH = "/settings/";
    private static final String DATA_MODEL_FILE = "DataModels.json";
    private static final String DATA_MODEL_TIER_FILE = "DataModelTiers.json";
    private static final String LIVING_MATTER_FILE = "LivingMatter.json";
    private static final String LIVING_MATTER_RECIPES_FILE = "LivingMatterRecipes.json";

    private static File configDir;

    private static final MetadataStore<MetadataDataModel> dataModelStore;
    private static final MetadataStore<MetadataDataModelTier> dataModelTierStore;
    private static final MetadataStore<MetadataLivingMatter> livingMatterStore;

    private static ImmutableList<JsonObject> livingMatterRecipesJson;

    static {
        dataModelStore = new MetadataStore<>(MetadataDataModel.class);
        dataModelTierStore = new MetadataStore<>(MetadataDataModelTier.class);
        livingMatterStore = new MetadataStore<>(MetadataLivingMatter.class);
    }

    public static void init(FMLPreInitializationEvent event) throws IOException {
        DMLRelearned.logger.info("Loading data from JSON config files...");
        configDir = new File(event.getModConfigurationDirectory(), DMLConstants.ModInfo.CONFIG_PATH);

        if (!configDir.exists() && !configDir.mkdirs()) {
            throw new IOException("Could not create mod config directory!");
        }

        copyConfigFile(DATA_MODEL_FILE)
                .flatMap(MetadataManager::readConfigFile)
                .ifPresent(dataModelStore::init);

        copyConfigFile(DATA_MODEL_TIER_FILE)
                .flatMap(MetadataManager::readConfigFile)
                .ifPresent(dataModelTierStore::init);

        copyConfigFile(LIVING_MATTER_FILE)
                .flatMap(MetadataManager::readConfigFile)
                .ifPresent(livingMatterStore::init);

        copyConfigFile(LIVING_MATTER_RECIPES_FILE)
                .flatMap(MetadataManager::readConfigFile)
                .ifPresent(MetadataManager::readLivingMatterRecipes);
    }

    public static void finalizeData() {
        DMLRelearned.logger.info("Finalizing config data...");
        dataModelStore.finalizeData();
        dataModelTierStore.finalizeData();
        livingMatterStore.finalizeData();
    }

    //
    // Copy and read JSON files
    //

    private static Optional<File> copyConfigFile(String filename) {
        File file = new File(configDir, filename);
        if (!file.exists()) {
            try (InputStream input = DMLRelearned.class.getResourceAsStream(INTERNAL_PATH + filename)) {
                Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                DMLRelearned.logger.error("Error extracting default config file \"{}\" from mod jar!", filename);
                return Optional.empty();
            }
        }
        return Optional.of(file);
    }

    private static Optional<JsonArray> readConfigFile(File file) {
        JsonElement result;
        FileReader fileReader;
        String filename = file.getName();
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            DMLRelearned.logger.error("Config file \"{}\" not found! Error message: {}", filename, e.getMessage());
            return Optional.empty();
        }

        try (JsonReader reader = new JsonReader(fileReader)) {
            JsonParser parser = new JsonParser();
            reader.setLenient(true);
            result = parser.parse(reader);
        } catch (Exception e) {
            if (e instanceof IOException)
                DMLRelearned.logger.error("Error reading config file \"{}\"! Error message: {}", filename, e.getMessage());
            else if (e instanceof JsonSyntaxException)
                DMLRelearned.logger.error("Invalid JSON in config file \"{}\"! Error message: {}", filename, e.getMessage());
            else
                DMLRelearned.logger.error("Exception while reading config file \"{}\"! Error message: {}", filename, e.getMessage());
            return Optional.empty();
        }

        if (!result.isJsonArray()) {
            DMLRelearned.logger.error("Error parsing config file \"{}\": root element must be an array!", filename);
            return Optional.empty();
        }

        return Optional.of(result.getAsJsonArray());
    }

    //
    // Data access (General)
    //

    public static ImmutableList<IRecipe> getCraftingRecipes() {
        ImmutableList.Builder<IRecipe> builder = ImmutableList.builder();

        // Get Data Model recipes from metadata
        dataModelStore.data.forEach((k, v) -> {
            if (DMLRHelper.isModLoaded(v.getModID()))
                v.getCraftingRecipe().ifPresent(builder::add);
        });

        // Construct Living Matter recipes from JSON data
        JsonContext ctx = new JsonContext(DMLConstants.ModInfo.ID);

        int recipeCount = 0;
        for(JsonObject entry : livingMatterRecipesJson) {
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

    //
    // Data Access (Data Models)
    //

    public static ImmutableList<MetadataDataModel> getDataModelMetadataList() {
        return dataModelStore.getMetadataList();
    }

    public static Optional<MetadataDataModel> getDataModelMetadata(String id) {
        return dataModelStore.get(id);
    }

    public static ImmutableMap<String, ResourceLocation> getDataModelTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataModelStore.data.forEach((k, v) -> {
            ResourceLocation dataModelTexture = v.getDataModelTexture();
            if (!dataModelTexture.equals(DMLConstants.DefaultModels.DATA_MODEL))
                builder.put(k, dataModelTexture);
        });

        return builder.build();
    }

    public static ImmutableMap<String, ResourceLocation> getPristineMatterTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataModelStore.data.forEach((k, v) -> {
            ResourceLocation pristineMatterTexture = v.getPristineMatterTexture();
            if (!pristineMatterTexture.equals(DMLConstants.DefaultModels.PRISTINE_MATTER))
                builder.put(k, pristineMatterTexture);
        });

        return builder.build();
    }

    public static ImmutableList<String> getAvailableTrials() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        dataModelStore.data.forEach((k, v) -> {
            if (v.getTrialData().hasEntity())
                builder.add(v.getDisplayName());
        });

        return builder.build();
    }

    //
    // Data Access (Data Model Tiers)
    //

    public static Optional<MetadataDataModelTier> getDataModelTierDataByTier(int tier) {
        return dataModelTierStore.get(String.valueOf(tier));
    }

    public static int getMaxDataModelTier() {
        return dataModelTierStore.size() - 1;
    }

    //
    // Data Access (Living Matter)
    //

    public static ImmutableList<MetadataLivingMatter> getLivingMatterMetadataList() {
        return livingMatterStore.getMetadataList();
    }

    public static Optional<MetadataLivingMatter> getLivingMatterMetadata(String id) {
        return livingMatterStore.get(id);
    }

    public static ImmutableMap<String, ResourceLocation> getLivingMatterTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        livingMatterStore.data.forEach((k, v) -> {
            ResourceLocation livingMatterTexture = v.getLivingMatterTexture();
            if (!livingMatterTexture.equals(DMLConstants.DefaultModels.LIVING_MATTER))
                builder.put(k, livingMatterTexture);
        });

        return builder.build();
    }

    private static void readLivingMatterRecipes(JsonArray array) {
        ImmutableList.Builder<JsonObject> builder = ImmutableList.builder();

        for (int i = 0; i < array.size(); i++) {
            JsonElement entry = array.get(i);
            if (entry.isJsonObject())
                builder.add(entry.getAsJsonObject());
            else
                DMLRelearned.logger.warn("Invalid entry at index {} in Data Model config (root array elements must be objects)", i);
        }

        livingMatterRecipesJson = builder.build();
    }
}
