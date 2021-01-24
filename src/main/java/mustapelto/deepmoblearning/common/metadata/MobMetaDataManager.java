package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelDataModel;
import mustapelto.deepmoblearning.common.util.FileHelper;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MobMetaDataManager {
    private static final LinkedHashMap<String, MobMetaData> dataStore = new LinkedHashMap<>();
    private static final String FILE_NAME = "DataModels.json";
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
            DMLRelearned.logger.error("Could not read default Data Model config file! This will cause the mod to malfunction.");
            return;
        }

        populateDataStore(dataObject);
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
                MobMetaData mobData = MobMetaData.create(entry.getKey(), contents.get(i).getAsJsonObject());
                if (mobData != null)
                    dataStore.put(mobData.getItemID(), mobData);
            }
        }
    }

    private static void writeConfigFile() {
        JsonObject data = new JsonObject();

        for (MobMetaData entry : dataStore.values()) {
            String modID = entry.getModID();
            if (!data.has(modID))
                data.add(modID, new JsonArray());
            data.get(modID).getAsJsonArray().add(entry.toJsonObject());
        }

        try {
            FileHelper.writeObject(data, configFile);
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not write Data Model config file!");
        }
    }

    public static LinkedHashMap<String, MobMetaData> getDataStore() {
        return dataStore;
    }

    public static ImmutableSet<ResourceLocation> getModelTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        dataStore.forEach((k, v) -> builder.add(v.getDataModelTexture()));

        return builder.build();
    }

    @Nullable
    public static MobMetaData getFromID(String id) {
        return dataStore.get(id);
    }
}
