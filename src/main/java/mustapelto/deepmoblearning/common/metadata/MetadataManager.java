package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by mustapelto on 2021-02-14
 * @param <T> Type of metadata
 */
public abstract class MetadataManager<T extends Metadata> {
    protected ImmutableMap<String, T> dataStore;
    private final String configFileName;

    public MetadataManager(String configFileName) {
        this.configFileName = configFileName;
    }

    public void loadData() {
        File configFile = new File(FileHelper.configDML, configFileName);
        if (!configFile.exists())
            FileHelper.copyFromJar("/settings/" + configFileName, configFile.toPath());

        JsonObject dataObject = readConfigFile(configFile);
        populateDataStore(dataObject);
    }

    public void finalizeData() {
        dataStore.values().forEach(Metadata::finalizeData);
    }

    private JsonObject readConfigFile(File configFile) {
        try {
            return FileHelper.readObject(configFile);
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not read config file! THis will cause the mod to malfunction." +
                    "Error message: {}", e.getMessage());
            return null;
        }
    }

    private void populateDataStore(JsonObject data) {
        if (data == null || !data.isJsonObject())
            return;

        ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();

        for (Map.Entry<String, JsonElement> category : data.entrySet()) {
            String categoryName = category.getKey();
            JsonElement categoryData = category.getValue();
            if (!categoryData.isJsonObject()) {
                DMLRelearned.logger.warn("Invalid category in JSON file, skipping! Filename: {}, Category: {}", configFileName, categoryName);
                continue;
            }

            for (Map.Entry<String, JsonElement> entry : categoryData.getAsJsonObject().entrySet()) {
                String entryName = entry.getKey();
                JsonElement entryData = entry.getValue();
                if (!entryData.isJsonObject()) {
                    DMLRelearned.logger.warn("Invalid entry in JSON file, skipping! Filename: {}, Entry: {}", configFileName, entryName);
                }
                T metadata = constructMetadataFromJson(entryData.getAsJsonObject(), categoryName, entryName);
                if (metadata != null)
                    builder.put(entryName, metadata);
            }
        }

        dataStore = builder.build();
    }

    protected abstract T constructMetadataFromJson(JsonObject data, String categoryName, String entryName);

    public ImmutableMap<String, T> getDataStore() {
        return dataStore;
    }
}
