package mustapelto.deepmoblearning.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerLivingMatter;
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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ModelLivingMatter implements IModel {
    public static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/living_matter_default");

    private final ResourceLocation livingMatterLocation;

    public ModelLivingMatter(ResourceLocation livingMatterLocation) {
        this.livingMatterLocation = livingMatterLocation;
    }

    @Override
    @Nonnull
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(livingMatterLocation);
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

        return (new ItemLayerModel(ImmutableList.of(livingMatterLocation))).bake(state, format, bakedTextureGetter);
    }

    public enum LoaderLivingMatter implements ICustomModelLoader {
        INSTANCE;

        private final Map<String, ResourceLocation> textureCache = new HashMap<>();
        private final Map<String, ModelLivingMatter> modelCache = new HashMap<>();

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getResourceDomain().equals(DMLConstants.ModInfo.ID)
                    && modelLocation.getResourcePath().contains("living_matter");
        }

        @Override
        @Nonnull
        public IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
            String livingMatterId = modelLocation.getResourcePath().substring("living_matter_".length());

            ModelLivingMatter model;
            if (!modelCache.containsKey(livingMatterId)) {
                model = new ModelLivingMatter(textureCache.getOrDefault(livingMatterId, DEFAULT_LOCATION));
                modelCache.put(livingMatterId, model);
            }
            return modelCache.get(livingMatterId);
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
            initTextureCache();
        }

        private void initTextureCache() {
            textureCache.clear();
            textureCache.putAll(MetadataManagerLivingMatter.INSTANCE.getLivingMatterTextures());
            modelCache.clear();
        }
    }
}
