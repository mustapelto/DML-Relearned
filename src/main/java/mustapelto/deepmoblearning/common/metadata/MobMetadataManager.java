package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelDataModel;
import mustapelto.deepmoblearning.client.models.ModelPristineMatter;
import mustapelto.deepmoblearning.common.util.FileHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MobMetadataManager {
    private static final LinkedHashMap<String, MobMetadata> dataStore = new LinkedHashMap<>();
    private static final String FILE_NAME = "DataModels.json";
    private static File configFile;

    public static void init() {
        configFile = new File(FileHelper.configDML, FILE_NAME);
        if (!configFile.exists())
            FileHelper.copyFromJar("/settings/" + FILE_NAME, configFile.toPath());

        readConfigFile();
    }

    private static void readConfigFile() {
        JsonObject dataObject;
        try {
            dataObject = FileHelper.readObject(configFile);
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not read Data Model config file! This will cause the mod to malfunction.");
            return;
        }

        populateDataStore(dataObject);
    }

    private static void populateDataStore(JsonObject data) {
        Set<Map.Entry<String, JsonElement>> entrySet = data.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            if (!(entry.getValue() instanceof JsonArray))
                continue;
            JsonArray contents = (JsonArray) entry.getValue();
            for (int i = 0; i < contents.size(); i++) {
                MobMetadata mobData = MobMetadata.deserialize(entry.getKey(), contents.get(i).getAsJsonObject());
                if (mobData != null)
                    dataStore.put(mobData.getItemID(), mobData);
            }
        }
    }

    public static LinkedHashMap<String, MobMetadata> getDataStore() {
        return dataStore;
    }

    public static ImmutableMap<String, ResourceLocation> getModelTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataStore.forEach((k, v) -> {
            ResourceLocation dataModelTexture = v.getDataModelTexture();
            if (!dataModelTexture.getResourcePath().equals(ModelDataModel.DEFAULT_LOCATION.getResourcePath()))
                builder.put(k, dataModelTexture);
        });

        return builder.build();
    }

    public static ImmutableMap<String, ResourceLocation> getPristineTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataStore.forEach((k, v) -> {
            ResourceLocation pristineMatterTexture = v.getPristineMatterTexture();
            if (!pristineMatterTexture.getResourcePath().equals(ModelPristineMatter.DEFAULT_LOCATION.getResourcePath()))
                builder.put(k, pristineMatterTexture);
        });

        return builder.build();
    }

    public static IRecipe[] getCraftingRecipes() {
        List<IRecipe> recipes = new ArrayList<>();

        dataStore.forEach((k, v) -> {
           if (v.isModLoaded())
               recipes.add(v.getCraftingRecipe());
        });

        return recipes.toArray(new IRecipe[0]);
    }
}
