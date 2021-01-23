package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.FileHelper;

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

        if (!configFile.exists()) {
            readDefaultFile();
            writeConfigFile();
            return;
        }

        readConfigFile();
    }

    private static void readDefaultFile() {
        JsonObject dataObject;
        try {
            dataObject = FileHelper.readObject("/settings/" + FILE_NAME);
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not read default Living Matter config file! This will cause the mod to malfunction.");
            return;
        }

        populateDataStore(dataObject);
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

    private static void populateDataStore(JsonObject data) {
        Set<Map.Entry<String, JsonElement>> entrySet = data.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            if (!(entry.getValue() instanceof JsonArray))
                continue;
            JsonArray contents = (JsonArray) entry.getValue();
            for (int i = 0; i < contents.size(); i++) {
                LivingMatterData livingMatterData = LivingMatterData.create(entry.getKey(), contents.get(i).getAsJsonObject());
                if (livingMatterData != null)
                    dataStore.put(livingMatterData.getItemID(), livingMatterData);
            }
        }
    }

    private static void writeConfigFile() {
        JsonObject data = new JsonObject();

        for (LivingMatterData entry : dataStore.values()) {
            String modID = entry.getModID();
            if (!data.has(modID))
                data.add(modID, new JsonArray());
            data.get(modID).getAsJsonArray().add(entry.toJsonObject());
        }

        try {
            FileHelper.writeObject(data, configFile);
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not write Living Matter config file!");
        }
    }

    public static LivingMatterData getByID(String id) {
        return dataStore.get(id);
    }

    public static LinkedHashMap<String, LivingMatterData> getDataStore() {
        return dataStore;
    }

    /**
     * @return first entry in dataStore
     */
    public static LivingMatterData getDefault() throws IllegalAccessException {
        if (dataStore.isEmpty())
            throw new IllegalAccessException("Can't get default item before store is initialized");
        return dataStore.entrySet().iterator().next().getValue();
    }
}
