package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelLivingMatter;
import net.minecraft.util.ResourceLocation;

/**
 * Created by mustapelto on 2021-02-14
 */
public class MetadataManagerLivingMatter extends MetadataManager<MetadataLivingMatter> {
    public static MetadataManagerLivingMatter INSTANCE = new MetadataManagerLivingMatter("LivingMatter.json");

    private MetadataManagerLivingMatter(String configFileName) {
        super(configFileName);
    }

    @Override
    public void loadData() {
        DMLRelearned.logger.info("Loading Living Matter data from JSON...");
        super.loadData();
    }

    @Override
    public void finalizeData() {
        DMLRelearned.logger.info("Finalizing Living Matter data...");
        super.finalizeData();
    }

    @Override
    protected MetadataLivingMatter constructMetadataFromJson(JsonObject data, String categoryName, String entryName) {
        return new MetadataLivingMatter(data, categoryName, entryName);
    }

    public ImmutableMap<String, ResourceLocation> getLivingMatterTextures() {
        ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

        dataStore.forEach((k, v) -> {
            ResourceLocation livingMatterTexture = v.getLivingMatterTexture();
            if (!livingMatterTexture.equals(ModelLivingMatter.DEFAULT_LOCATION))
                builder.put(k, livingMatterTexture);
        });

        return builder.build();
    }
}
