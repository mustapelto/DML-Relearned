package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelDataModel;
import mustapelto.deepmoblearning.client.models.ModelPristineMatter;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

/**
 * Created by mustapelto on 2021-02-14
 */
public class MetadataManagerDataModels extends MetadataManager<MetadataDataModel> {
    public static MetadataManagerDataModels INSTANCE = new MetadataManagerDataModels("DataModels.json");

    private MetadataManagerDataModels(String configFileName) {
        super(configFileName);
    }

    @Override
    public void loadData() {
        DMLRelearned.logger.info("Loading Data Model data from JSON...");
        super.loadData();
    }

    @Override
    public void finalizeData() {
        DMLRelearned.logger.info("Finalizing Data Model data...");
        super.finalizeData();
    }

    @Override
    protected MetadataDataModel constructMetadataFromJson(JsonObject data, String categoryName, String entryName) {
        return new MetadataDataModel(data, categoryName, entryName);
    }

    public ImmutableMap<String, ResourceLocation> getDataModelTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataStore.forEach((k, v) -> {
            ResourceLocation dataModelTexture = v.getDataModelTexture();
            if (!dataModelTexture.equals(ModelDataModel.DEFAULT_LOCATION))
                builder.put(k, dataModelTexture);
        });

        return builder.build();
    }

    public ImmutableMap<String, ResourceLocation> getPristineMatterTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataStore.forEach((k, v) -> {
            ResourceLocation pristineMatterTexture = v.getPristineMatterTexture();
            if (!pristineMatterTexture.equals(ModelPristineMatter.DEFAULT_LOCATION))
                builder.put(k, pristineMatterTexture);
        });

        return builder.build();
    }

    public ImmutableList<IRecipe> getCraftingRecipes() {
        ImmutableList.Builder<IRecipe> builder = ImmutableList.builder();

        dataStore.forEach((k, v) -> {
            if (v.isModLoaded())
                builder.add(v.getCraftingRecipe());
        });

        return builder.build();
    }
}
