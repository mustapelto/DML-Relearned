package mustapelto.deepmoblearning.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.metadata.MetadataManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModelDataModel implements IModel {
    private static final ResourceLocation BASE_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_base");
    private static final ResourceLocation BLANK_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_blank");

    private final ResourceLocation mobLocation;

    public ModelDataModel(ResourceLocation mobLocation) {
        this.mobLocation = mobLocation;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(BASE_LOCATION, mobLocation);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.of();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        state = ForgeBlockStateV1.Transforms.get("forge:default-item").orElse(state);
        ItemLayerModel model = new ItemLayerModel(ImmutableList.of(BASE_LOCATION, mobLocation));
        return model.bake(state, format, bakedTextureGetter);
    }

    public enum LoaderDataModel implements ICustomModelLoader {
        INSTANCE;

        private final Map<String, ResourceLocation> textureCache = new HashMap<>();
        private final Map<String, ModelDataModel> modelCache = new HashMap<>();

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getNamespace().equals(DMLConstants.ModInfo.ID)
                    && modelLocation.getPath().contains("data_model");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            String mobId = modelLocation.getPath().substring("data_model_".length());

            ModelDataModel model;
            if (!modelCache.containsKey(mobId)) {
                model = new ModelDataModel(textureCache.getOrDefault(mobId, DMLConstants.DefaultModels.DATA_MODEL));
                modelCache.put(mobId, model);
            }
            return modelCache.get(mobId);
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            initTextureCache();
        }

        private void initTextureCache() {
            textureCache.clear();
            textureCache.put("blank", BLANK_LOCATION);
            textureCache.putAll(MetadataManager.getDataModelTextures());
            modelCache.clear();
        }
    }
}
