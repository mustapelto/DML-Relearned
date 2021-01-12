package mustapelto.deepmoblearning.common.mobdata;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import mustapelto.deepmoblearning.common.enums.EnumMobType;
import mustapelto.deepmoblearning.common.mobdata.mo.MOAndroidMetaData;
import mustapelto.deepmoblearning.common.mobdata.thermal.ThermalElementalMetaData;
import mustapelto.deepmoblearning.common.mobdata.tinkers.TinkerSlimeMetaData;
import mustapelto.deepmoblearning.common.mobdata.twilight.TwilightDarkwoodMetaData;
import mustapelto.deepmoblearning.common.mobdata.twilight.TwilightForestMetaData;
import mustapelto.deepmoblearning.common.mobdata.twilight.TwilightGlacierMetaData;
import mustapelto.deepmoblearning.common.mobdata.twilight.TwilightSwampMetaData;
import mustapelto.deepmoblearning.common.mobdata.vanilla.*;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MobMetaDataStore {
    private static final HashMap<EnumMobType, MobMetaData> store = new HashMap<>();

    static {
        store.put(EnumMobType.BLAZE, BlazeMetaData.getInstance());
        store.put(EnumMobType.CREEPER, CreeperMetaData.getInstance());
        store.put(EnumMobType.ENDER_DRAGON, EnderDragonMetaData.getInstance());
        store.put(EnumMobType.ENDERMAN, EndermanMetaData.getInstance());
        store.put(EnumMobType.GHAST, GhastMetaData.getInstance());
        store.put(EnumMobType.GUARDIAN, GuardianMetaData.getInstance());
        store.put(EnumMobType.SHULKER, ShulkerMetaData.getInstance());
        store.put(EnumMobType.SKELETON, SkeletonMetaData.getInstance());
        store.put(EnumMobType.SLIME, SlimeMetaData.getInstance());
        store.put(EnumMobType.SPIDER, SpiderMetaData.getInstance());
        store.put(EnumMobType.WITCH, WitchMetaData.getInstance());
        store.put(EnumMobType.WITHER, WitherMetaData.getInstance());
        store.put(EnumMobType.WITHERSKELETON, WitherSkeletonMetaData.getInstance());
        store.put(EnumMobType.ZOMBIE, ZombieMetaData.getInstance());

        //
        // THERMAL EXPANSION
        //
        if (DMLConstants.ModDependencies.isLoaded(DMLConstants.ModDependencies.THERMAL)) {
            store.put(EnumMobType.THERMAL, ThermalElementalMetaData.getInstance());
        }

        //
        // TINKERS' CONSTRUCT
        //
        if (DMLConstants.ModDependencies.isLoaded(DMLConstants.ModDependencies.TINKERS)) {
            store.put(EnumMobType.TINKERSLIME, TinkerSlimeMetaData.getInstance());
        }

        //
        // TWILIGHT FOREST
        //
        if (DMLConstants.ModDependencies.isLoaded(DMLConstants.ModDependencies.TWILIGHT)) {
            store.put(EnumMobType.TWILIGHTFOREST, TwilightForestMetaData.getInstance());
            store.put(EnumMobType.TWILIGHTSWAMP, TwilightSwampMetaData.getInstance());
            store.put(EnumMobType.TWILIGHTDARKWOOD, TwilightDarkwoodMetaData.getInstance());
            store.put(EnumMobType.TWILIGHTGLACIER, TwilightGlacierMetaData.getInstance());
        }

        //
        // MATTER OVERDRIVE
        //
        if (DMLConstants.ModDependencies.isLoaded(DMLConstants.ModDependencies.MO)) {
            store.put(EnumMobType.MO_ANDROID, MOAndroidMetaData.getInstance());
        }
    }

    public static int getSimulationTickCost(EnumMobType mobType) {
        return DMLConfig.SIMULATION_CHAMBER.getSimulationTickCost(mobType);
    }

    private static MobMetaData getMetaData(EnumMobType mobType) {
        return store.getOrDefault(mobType, null);
    }

    public static String getDisplayName(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getName() : "";
    }

    public static String getDisplayNamePlural(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getPluralName() : "";
    }

    public static String[] getMobTrivia(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getMobTrivia() : new String[] {};
    }

    public static int getNumberOfHearts(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getNumberOfHearts() : 0;
    }

    public static String getExtraTooltip(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getExtraTooltip() : "";
    }

    public static int getInterfaceScale(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getInterfaceScale() : 0;
    }

    public static int getInterfaceOffsetX(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getInterfaceOffsetX() : 0;
    }

    public static int getInterfaceOffsetY(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getInterfaceOffsetY() : 0;
    }

    @Nullable
    public static EnumLivingMatterType getLivingMatterType(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getLivingMatterType() : null;
    }

    @Nullable
    public static Entity getEntity(EnumMobType mobType, World world) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData != null ? metaData.getEntity(world) : null;
    }

    @Nullable
    public static Entity getEntityExtra(EnumMobType mobType, World world) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData instanceof MobMetaData.MobMetaDataExtra ? ((MobMetaData.MobMetaDataExtra) metaData).getEntityExtra(world) : null;
    }

    public static int getExtraInterfaceOffsetX(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData instanceof MobMetaData.MobMetaDataExtra ? ((MobMetaData.MobMetaDataExtra) metaData).getExtraInterfaceOffsetX() : 0;
    }

    public static int getExtraInterfaceOffsetY(EnumMobType mobType) {
        MobMetaData metaData = getMetaData(mobType);
        return metaData instanceof MobMetaData.MobMetaDataExtra ? ((MobMetaData.MobMetaDataExtra) metaData).getExtraInterfaceOffsetY() : 0;
    }
}
