package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

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

    public ImmutableSet<ResourceLocation> getDataModelTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        dataStore.values().forEach(metadata -> {
            Optional<ResourceLocation> dataModelTexture = metadata.getDataModelTexture();
            if (!dataModelTexture.isPresent())
                DMLRelearned.logger.info("Data Model texture not found for entry {}:{}. Using default texture.", metadata.categoryID, metadata.metadataID);
            else
                builder.add(dataModelTexture.get());
        });

        return builder.build();
    }

    public ImmutableMap<String, ResourceLocation> getPristineMatterTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataStore.forEach((k, v) -> {
            ResourceLocation pristineMatterTexture = v.getPristineMatterTexture();
            if (!pristineMatterTexture.equals(DMLConstants.DefaultModels.PRISTINE_MATTER))
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

    public ImmutableList<String> getAvailableTrials() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        dataStore.forEach((k, v) -> {
            if (v.getTrialData().hasEntity())
                builder.add(v.getDisplayName());
        });

        return builder.build();
    }
}
