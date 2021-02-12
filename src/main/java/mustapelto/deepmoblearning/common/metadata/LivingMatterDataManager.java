package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelDataModel;
import mustapelto.deepmoblearning.common.util.FileHelper;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LivingMatterDataManager {
    private static final LinkedHashMap<String, LivingMatterData> dataStore = new LinkedHashMap<>();
    private static final String FILE_NAME = "LivingMatter.json";
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
            DMLRelearned.logger.error("Could not read Living Matter config file! This will cause the mod to malfunction.");
            return;
        }

        populateDataStore(dataObject);
    }

    /**
     * @param data JSON object containing data to put into store
     */
    private static void populateDataStore(JsonObject data) {
        Set<Map.Entry<String, JsonElement>> entrySet = data.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            if (!(entry.getValue() instanceof JsonArray))
                continue;
            JsonArray contents = (JsonArray) entry.getValue();
            for (int i = 0; i < contents.size(); i++) {
                LivingMatterData livingMatterData = LivingMatterData.deserialize(entry.getKey(), contents.get(i).getAsJsonObject());
                if (livingMatterData != null)
                    dataStore.put(livingMatterData.getItemID(), livingMatterData);
            }
        }
    }

    /**
     * @param id id of living matter type
     * @return living matter data entry with given id, or first in list if invalid id
     */
    public static LivingMatterData getByID(String id) {
        return dataStore.getOrDefault(id, dataStore.entrySet().iterator().next().getValue());
    }

    public static LinkedHashMap<String, LivingMatterData> getDataStore() {
        return dataStore;
    }

    public static ImmutableMap<String, ResourceLocation> getLivingMatterTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataStore.forEach((k, v) -> {
            ResourceLocation livingMatterTexture = v.getLivingMatterTexture();
            if (!livingMatterTexture.getResourcePath().equals(ModelDataModel.DEFAULT_LOCATION.getResourcePath()))
                builder.put(k, livingMatterTexture);
        });

        return builder.build();
    }
}
