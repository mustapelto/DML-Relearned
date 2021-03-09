package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

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

        readConfigFile(configFile).ifPresent(this::populateDataStore);
    }

    public void finalizeData() {
        dataStore.values().forEach(Metadata::finalizeData);
    }

    private Optional<JsonObject> readConfigFile(File configFile) {
        try {
            return Optional.of(FileHelper.readObject(configFile));
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not read config file! THis will cause the mod to malfunction." +
                    "Error message: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private void populateDataStore(JsonObject data) {
        if (!data.isJsonObject())
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
                    continue;
                }

                builder.put(entryName, constructMetadataFromJson(entryData.getAsJsonObject(), categoryName, entryName));
            }
        }

        dataStore = builder.build();
    }

    protected abstract T constructMetadataFromJson(JsonObject data, String categoryName, String entryName);

    public Optional<T> getByKey(String key) {
        T result = dataStore.get(key);
        return result != null ? Optional.of(result) : Optional.empty();
    }

    public ImmutableMap<String, T> getDataStore() {
        return dataStore;
    }
}
