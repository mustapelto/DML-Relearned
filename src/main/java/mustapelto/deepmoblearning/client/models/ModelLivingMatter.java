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

public class ModelLivingMatter implements IModel {
    private final ResourceLocation livingMatterLocation;

    public ModelLivingMatter(ResourceLocation livingMatterLocation) {
        this.livingMatterLocation = livingMatterLocation;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(livingMatterLocation);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.of();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        state = ForgeBlockStateV1.Transforms.get("forge:default-item").orElse(state);
        ItemLayerModel model = new ItemLayerModel(ImmutableList.of(livingMatterLocation));
        return model.bake(state, format, bakedTextureGetter);
    }

    public enum LoaderLivingMatter implements ICustomModelLoader {
        INSTANCE;

        private final Map<String, ResourceLocation> textureCache = new HashMap<>();
        private final Map<String, ModelLivingMatter> modelCache = new HashMap<>();

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getNamespace().equals(DMLConstants.ModInfo.ID)
                    && modelLocation.getPath().contains("living_matter");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            String livingMatterId = modelLocation.getPath().substring("living_matter_".length());

            ModelLivingMatter model;
            if (!modelCache.containsKey(livingMatterId)) {
                model = new ModelLivingMatter(textureCache.getOrDefault(livingMatterId, DMLConstants.DefaultModels.LIVING_MATTER));
                modelCache.put(livingMatterId, model);
            }
            return modelCache.get(livingMatterId);
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            initTextureCache();
        }

        private void initTextureCache() {
            textureCache.clear();
            textureCache.putAll(MetadataManager.getLivingMatterTextures());
            modelCache.clear();
        }
    }
}
