package mustapelto.deepmoblearning.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.metadata.MobMetaDataManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class ModelDynDataModel implements IModel {
    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_default");
    private static final ResourceLocation BLANK_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_blank");

    private static final float NORTH_Z_MOB = 7.498f / 16f;
    private static final float SOUTH_Z_MOB = 8.502f / 16f;

    private static final ResourceLocation BASE_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_base");

    private final ResourceLocation mobLocation;

    public ModelDynDataModel(@Nonnull ResourceLocation mobLocation) {
        this.mobLocation = mobLocation;
    }

    @Override
    @Nonnull
    public Collection<ResourceLocation> getTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        builder.add(BASE_LOCATION);
        builder.add(mobLocation);

        return builder.build();
    }

    @Override
    @Nonnull
    public IBakedModel bake(@Nonnull IModelState state, @Nonnull VertexFormat format, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

        TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());

        TextureAtlasSprite particleSprite;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        IBakedModel model = (new ItemLayerModel(ImmutableList.of(BASE_LOCATION))).bake(state, format, bakedTextureGetter);
        builder.addAll(model.getQuads(null, null, 0));

        TextureAtlasSprite mobSprite = bakedTextureGetter.apply(mobLocation);
        builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, mobSprite, mobSprite, NORTH_Z_MOB, EnumFacing.NORTH, 0xFFFFFFFF, 1));
        builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, mobSprite, mobSprite, SOUTH_Z_MOB, EnumFacing.SOUTH, 0xFFFFFFFF, 1));
        particleSprite = mobSprite;

        //return new BakedDynDataModel(this, builder.build(), particleSprite, format, Maps.immutableEnumMap(transformMap), Maps.newHashMap(), transform.isIdentity());
        return new BakedItemModel(builder.build(), particleSprite, transformMap, ItemOverrideList.NONE, transform.isIdentity());
    }

    @Override
    @Nonnull
    public IModel retexture(ImmutableMap<String, String> textures) {
        ResourceLocation mob = mobLocation;

        if (textures.containsKey("mob"))
            mob = new ResourceLocation(textures.get("mob"));

        return new ModelDynDataModel(mob);
    }

    public static class LoaderDynDataModel implements ICustomModelLoader {
        public static final LoaderDynDataModel INSTANCE = new LoaderDynDataModel();

        private final Map<String, ResourceLocation> availableTextures = new HashMap<>();

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getResourceDomain().equals(DMLConstants.ModInfo.ID) && modelLocation.getResourcePath().contains("data_model");
        }

        @Override
        @Nonnull
        public IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
            return new ModelDynDataModel(availableTextures.getOrDefault(modelLocation.getResourcePath(), DEFAULT_LOCATION));
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
            rebuildTextures();
        }


        /**
         * Register available data model textures
         */
        private void rebuildTextures() {
            availableTextures.clear();
            availableTextures.put("data_model_blank", BLANK_LOCATION);
            MobMetaDataManager.getDataStore().forEach((k, v) -> {
                ResourceLocation mobLocation = v.getDataModelTexture();
                if (mobLocation != null) {
                    availableTextures.put(v.getModelName(), mobLocation);
                }
            });
        }
    }
}