package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;

/**
 * Created by mustapelto on 2021-02-14
 */
public class MetadataManagerDataModelTiers extends MetadataManager<MetadataDataModelTier> {
    public static MetadataManagerDataModelTiers INSTANCE = new MetadataManagerDataModelTiers("DataModelTiers.json");

    private MetadataManagerDataModelTiers(String configFileName) {
        super(configFileName);
    }

    @Override
    public void loadData() {
        DMLRelearned.logger.info("Loading Data Model Tier data from JSON...");
        super.loadData();
    }

    @Override
    protected MetadataDataModelTier constructMetadataFromJson(JsonObject data, String categoryName, String entryName) {
        return new MetadataDataModelTier(data, entryName);
    }

    public MetadataDataModelTier getByLevel(int level) {
        return dataStore.getOrDefault(String.valueOf(level), MetadataDataModelTier.INVALID);
    }

    public int getMaxLevel() {
        return dataStore.size() - 1;
    }
}
