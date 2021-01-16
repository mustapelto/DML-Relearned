package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonArray;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LivingMatterDataManager {
    private static HashMap<String, LivingMatterData> dataStore;

    public static void init() {
        File jsonFile = new File(FileHelper.configDML, "LivingMatter.json");

        if (!jsonFile.exists()) {
            generateDefaultDataFile(jsonFile);
        }

        dataStore = new HashMap<>();

        readDataFromFile(jsonFile);
    }

    private static void generateDefaultDataFile(File jsonFile) {
        JsonArray dataArray = new JsonArray();
        dataArray.add(LivingMatterData.createJsonObject(DMLConstants.LivingMatter.DEFAULT_VALUES.OVERWORLDIAN.ID,
                "minecraft",
                "Overworldian",
                "§aOverworldian§r",
                DMLConstants.LivingMatter.DEFAULT_VALUES.OVERWORLDIAN.XP));
        dataArray.add(LivingMatterData.createJsonObject(DMLConstants.LivingMatter.DEFAULT_VALUES.HELLISH.ID,
                "minecraft",
                "Hellish",
                "§cHellish§r",
                DMLConstants.LivingMatter.DEFAULT_VALUES.HELLISH.XP));
        dataArray.add(LivingMatterData.createJsonObject(DMLConstants.LivingMatter.DEFAULT_VALUES.EXTRATERRESTRIAL.ID,
                "minecraft",
                "Extraterrestrial",
                "§dExtraterrestrial§r",
                DMLConstants.LivingMatter.DEFAULT_VALUES.EXTRATERRESTRIAL.XP));
        dataArray.add(LivingMatterData.createJsonObject(DMLConstants.LivingMatter.DEFAULT_VALUES.TWILIGHT.ID,
                "twilightforest",
                "Twilight",
                "§dTwilight§r",
                DMLConstants.LivingMatter.DEFAULT_VALUES.TWILIGHT.XP));

        try {
            FileHelper.writeArray(dataArray, jsonFile);
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not write default Living Matter config file!");
        }
    }

    private static void readDataFromFile(File jsonFile) {
        JsonArray dataArray;
        try {
            dataArray = FileHelper.readArray(jsonFile);
        } catch (IOException e) {
            DMLRelearned.logger.error("Could not read Living Matter config file! This will cause the mod to malfunction.");
            return;
        }

        dataArray.forEach(block -> {
            LivingMatterData data = new LivingMatterData(block.getAsJsonObject());
            dataStore.put(data.itemID, data);
        });
    }

    public static LivingMatterData getByID(String id) {
        return dataStore.get(id);
    }

    public static HashMap<String, LivingMatterData> getDataStore() {
        return dataStore;
    }
}
