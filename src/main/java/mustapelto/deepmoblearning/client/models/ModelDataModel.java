package mustapelto.deepmoblearning.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.metadata.MobMetaData;
import mustapelto.deepmoblearning.common.metadata.MobMetaDataManager;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ModelDataModel implements IModel {
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(DMLConstants.ModInfo.ID, "dyn_data_model"), "inventory");
    public static final ResourceLocation DEFAULT_MOB_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_default");
    private static final ResourceLocation BASE_LOCATION = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_base");

    protected static final IModel BASE_MODEL = new ModelDataModel();

    private final ResourceLocation mobLocation;

    public ModelDataModel()
    {
        this(DEFAULT_MOB_LOCATION);
    }

    public ModelDataModel(ResourceLocation mobLocation)
    {
        this.mobLocation = mobLocation;
    }

    @Override
    @Nonnull
    public Collection<ResourceLocation> getTextures()
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        builder.add(BASE_LOCATION);
        builder.add(DEFAULT_MOB_LOCATION);
        builder.addAll(MobMetaDataManager.getModelTextures());

        return builder.build();
    }

    @Override
    @Nonnull
    public IBakedModel bake(@Nonnull IModelState state,
                            @Nonnull VertexFormat format,
                            @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);
        TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());

        IBakedModel model = new ItemLayerModel(ImmutableList.of(BASE_LOCATION, mobLocation)).bake(state, format, bakedTextureGetter);
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        builder.addAll(model.getQuads(null, null, 0));

        return new BakedDataModel(this, builder.build(), model.getParticleTexture(), format, transformMap, Maps.newHashMap(), transform.isIdentity());
    }

    @Override
    @Nonnull
    public ModelDataModel process(ImmutableMap<String, String> customData)
    {
        String mobName = customData.get("mob");
        ResourceLocation newMobLocation;
        MobMetaData mobData = MobMetaDataManager.getFromID(mobName);
        if (mobData == null)
            newMobLocation = DEFAULT_MOB_LOCATION;
        else
            newMobLocation = mobData.getDataModelTexture();

        return new ModelDataModel(newMobLocation);
    }

    public enum LoaderDataModel implements ICustomModelLoader
    {
        INSTANCE;

        private static final Map<String, IBakedModel> modelCache = new HashMap<>();

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals(DMLConstants.ModInfo.ID) && modelLocation.getResourcePath().contains("dml_data_model");
        }

        @Override
        @Nonnull
        public IModel loadModel(@Nonnull ResourceLocation modelLocation)
        {
            return BASE_MODEL;
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {}
    }

    private static final class BakedDataModelOverrideHandler extends ItemOverrideList
    {
        public static final BakedDataModelOverrideHandler INSTANCE = new BakedDataModelOverrideHandler();
        private BakedDataModelOverrideHandler()
        {
            super(ImmutableList.of());
        }

        @Override
        @Nonnull
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
        {
            MobMetaData mobData = DataModelHelper.getMobMetaData(stack);

            if (mobData == null)
            {
                return originalModel;
            }

            BakedDataModel model = (BakedDataModel) originalModel;

            String mobName = mobData.getItemID();

            if (!model.cache.containsKey(mobName))
            {
                IModel parent = model.parent.process(ImmutableMap.of("mob", mobName));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.getTransforms()), model.format, textureGetter);
                model.cache.put(mobName, bakedModel);
                return bakedModel;
            }

            return model.cache.get(mobName);
        }
    }

    private static final class BakedDataModel extends BakedItemModel
    {
        private final ModelDataModel parent;
        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
        private final VertexFormat format;

        BakedDataModel(ModelDataModel parent,
                       ImmutableList<BakedQuad> quads,
                       TextureAtlasSprite particle,
                       VertexFormat format,
                       ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
                       Map<String, IBakedModel> cache,
                       boolean untransformed)
        {
            super(quads, particle, transforms, BakedDataModelOverrideHandler.INSTANCE, untransformed);
            this.format = format;
            this.parent = parent;
            this.cache = cache;
        }

        public ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms()
        {
            return transforms;
        }
    }
}