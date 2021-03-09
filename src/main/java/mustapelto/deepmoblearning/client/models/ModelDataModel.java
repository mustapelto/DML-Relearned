package mustapelto.deepmoblearning.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.metadata.MetadataManagerDataModels;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ModelDataModel implements IModel {
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(DMLConstants.ModInfo.ID, "data_model"), "inventory");

    private static final float NORTH_Z_MOB = 7.498f / 16f;
    private static final float SOUTH_Z_MOB = 8.502f / 16f;

    private static final ResourceLocation BASE_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_base");
    private static final ResourceLocation BLANK_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_blank");
    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_default");

    private static final String METADATA_KEY = "metadata";

    public static final ModelDataModel MODEL = new ModelDataModel();

    private final String metadataID;

    public ModelDataModel() {
        this("");
    }

    public ModelDataModel(String metadataID) {
        this.metadataID = metadataID;
    }

    @Override
    @Nonnull
    public Collection<ResourceLocation> getTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        builder.add(BASE_LOCATION);
        builder.add(BLANK_LOCATION);
        builder.add(DEFAULT_LOCATION);
        builder.addAll(MetadataManagerDataModels.INSTANCE.getDataModelTextures());

        return builder.build();
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

        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);
        TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());

        TextureAtlasSprite mobSprite;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        ResourceLocation mobTextureLocation = MetadataManagerDataModels.INSTANCE.getByKey(metadataID)
                .map(data -> data.getDataModelTexture().orElse(DEFAULT_LOCATION))
                .orElse(BLANK_LOCATION);

        mobSprite = bakedTextureGetter.apply(mobTextureLocation);

        IBakedModel model = (new ItemLayerModel(ImmutableList.of(BASE_LOCATION))).bake(state, format, bakedTextureGetter);
        builder.addAll(model.getQuads(null, null, 0));
        builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_MOB, mobSprite, EnumFacing.NORTH, 0xFFFFFFFF, 0xFFFFFFFF));
        builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_MOB, mobSprite, EnumFacing.SOUTH, 0xFFFFFFFF, 0xFFFFFFFF));

        return new Baked(this, builder.build(), mobSprite, format, Maps.immutableEnumMap(transformMap), Maps.newHashMap());
    }

    @Override
    @Nonnull
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    @Override
    @Nonnull
    public IModel process(@Nonnull ImmutableMap<String, String> customData) {
        String metadataID = customData.get(METADATA_KEY);

        if (metadataID == null)
            metadataID = this.metadataID;

        return new ModelDataModel(metadataID);
    }

    @Override
    @Nonnull
    public IModel retexture(@Nonnull ImmutableMap<String, String> textures) {
        return new ModelDataModel(metadataID);
    }

    public enum LoaderDataModel implements ICustomModelLoader {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getResourceDomain().equals(DMLConstants.ModInfo.ID)
                    && modelLocation.getResourcePath().contains("data_model");
        }

        @Override
        @Nonnull
        public IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {}
    }

    private static final class BakedOverrideHandler extends ItemOverrideList {
        public static final BakedOverrideHandler INSTANCE = new BakedOverrideHandler();

        private BakedOverrideHandler() {
            super(ImmutableList.of());
        }

        @Override
        @Nonnull
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            String metadataID = NBTHelper.getString(stack, ItemDataModel.NBT_METADATA_KEY, "");

            if (metadataID.isEmpty()) {
                return originalModel;
            }

            Baked model = (Baked)originalModel;

            if (!model.cache.containsKey(metadataID)) {
                IModel parent = model.parent.process(ImmutableMap.of(METADATA_KEY, metadataID));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                    @Override
                    public TextureAtlasSprite apply(ResourceLocation resourceLocation) {
                        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(resourceLocation.toString());
                    }
                };

                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format, textureGetter);
                model.cache.put(metadataID, bakedModel);
                return bakedModel;
            }

            return model.cache.get(metadataID);
        }
    }

    private static final class Baked implements IBakedModel {
        private final ModelDataModel parent;
        private final ImmutableList<BakedQuad> quads;
        private final TextureAtlasSprite particle;
        private final VertexFormat format;
        private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
        private final Map<String, IBakedModel> cache;

        public Baked(ModelDataModel parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
            this.parent = parent;
            this.quads = quads;
            this.particle = particle;
            this.format = format;
            this.transforms = transforms;
            this.cache = cache;
        }

        @Override
        @Nonnull
        public ItemOverrideList getOverrides() {
            return BakedOverrideHandler.INSTANCE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull ItemCameraTransforms.TransformType cameraTransformType) {
            return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
        }

        @Override
        @Nonnull
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            if (side == null)
                return quads;
            return ImmutableList.of();
        }

        @Override
        public boolean isAmbientOcclusion(@Nonnull IBlockState state) {
            return true;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        @Nonnull
        public TextureAtlasSprite getParticleTexture() {
            return particle;
        }
    }
}
