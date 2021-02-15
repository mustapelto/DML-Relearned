package mustapelto.deepmoblearning.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModels;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

public class ModelDataModel implements IModel {
    private static final ResourceLocation BASE_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_base");
    private static final ResourceLocation BLANK_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_blank");

    private final ResourceLocation mobLocation;

    public ModelDataModel(ResourceLocation mobLocation) {
        this.mobLocation = mobLocation;
    }

    @Override
    @Nonnull
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(BASE_LOCATION, mobLocation);
    }

    @Override
    @Nonnull
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.of();
    }

    @Override
    @Nonnull
    public IBakedModel bake(@Nonnull IModelState state, @Nonnull VertexFormat format, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        Optional<IModelState> itemGenerated = ForgeBlockStateV1.Transforms.get("forge:default-item");
        if (itemGenerated.isPresent())
            state = itemGenerated.get();

        return (new ItemLayerModel(ImmutableList.of(BASE_LOCATION, mobLocation))).bake(state, format, bakedTextureGetter);
    }

    public enum LoaderDataModel implements ICustomModelLoader {
        INSTANCE;

        private final Map<String, ResourceLocation> textureCache = new HashMap<>();
        private final Map<String, ModelDataModel> modelCache = new HashMap<>();

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getResourceDomain().equals(DMLConstants.ModInfo.ID)
                    && modelLocation.getResourcePath().contains("data_model");
        }

        @Override
        @Nonnull
        public IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
            String mobId = modelLocation.getResourcePath().substring("data_model_".length());

            ModelDataModel model;
            if (!modelCache.containsKey(mobId)) {
                model = new ModelDataModel(textureCache.getOrDefault(mobId, DMLConstants.DefaultModels.DATA_MODEL));
                modelCache.put(mobId, model);
            }
            return modelCache.get(mobId);
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
            initTextureCache();
        }

        private void initTextureCache() {
            textureCache.clear();
            textureCache.put("blank", BLANK_LOCATION);
            textureCache.putAll(MetadataManagerDataModels.INSTANCE.getDataModelTextures());
            modelCache.clear();
        }
    }
}
