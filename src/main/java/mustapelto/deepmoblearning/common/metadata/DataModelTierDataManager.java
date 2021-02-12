package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public class DataModelTierDataManager {
    private static final LinkedHashMap<Integer, DataModelTierData> dataStore = new LinkedHashMap<>();
    private static final String FILE_NAME = "DataModelTiers.json";
    private static File configFile;
    private static int maxLevel = -1; // First added tier is "Tier 0"

    public static void init() {
        configFile = new File(FileHelper.configDML, FILE_NAME);
        if (!configFile.exists())
            FileHelper.copyFromJar("/settings/" + FILE_NAME, configFile.toPath());

        readConfigFile();
    }

    private static void readConfigFile() {
        JsonArray dataArray;
        try {
            dataArray = FileHelper.readArray(configFile);
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not read Data Model Tier config file! This will cause the mod to malfunction.");
            return;
        }

        populateDataStore(dataArray);
    }

    private static void populateDataStore(JsonArray data) {
        for (int i = 0; i < data.size(); i++) {
            JsonObject element = data.get(i).getAsJsonObject();
            DataModelTierData dataModelTierData = DataModelTierData.deserialize(i, element);
            dataStore.put(i, dataModelTierData);
            maxLevel++;
        }
    }

    public static DataModelTierData getByLevel(int level) {
        return dataStore.get(level);
    }

    public static int getMaxLevel() {
        return maxLevel;
    }
}
