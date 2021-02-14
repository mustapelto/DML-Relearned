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

public class ModelPristineMatter implements IModel {
    public static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/pristine_matter_default");

    private final ResourceLocation mobLocation;

    public ModelPristineMatter(ResourceLocation mobLocation) {
        this.mobLocation = mobLocation;
    }

    @Override
    @Nonnull
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(mobLocation);
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

        return (new ItemLayerModel(ImmutableList.of(mobLocation))).bake(state, format, bakedTextureGetter);
    }

    public enum LoaderPristineMatter implements ICustomModelLoader {
        INSTANCE;

        private final Map<String, ResourceLocation> textureCache = new HashMap<>();
        private final Map<String, ModelPristineMatter> modelCache = new HashMap<>();

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getResourceDomain().equals(DMLConstants.ModInfo.ID)
                    && modelLocation.getResourcePath().contains("pristine_matter");
        }

        @Override
        @Nonnull
        public IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
            String mobId = modelLocation.getResourcePath().substring("pristine_matter_".length());

            ModelPristineMatter model;
            if (!modelCache.containsKey(mobId)) {
                model = new ModelPristineMatter(textureCache.getOrDefault(mobId, DEFAULT_LOCATION));
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
            textureCache.putAll(MetadataManagerDataModels.INSTANCE.getPristineMatterTextures());
            modelCache.clear();
        }
    }
}
