package mustapelto.deepmoblearning.common.mobdata;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.mobdata.vanilla.*;
import mustapelto.deepmoblearning.common.mobdata.mo.*;
import mustapelto.deepmoblearning.common.mobdata.thermal.*;
import mustapelto.deepmoblearning.common.mobdata.tinkers.*;
import mustapelto.deepmoblearning.common.mobdata.twilight.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Arrays;
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
        store.put(EnumMobType.WITHER_SKELETON, WitherSkeletonMetaData.getInstance());
        store.put(EnumMobType.ZOMBIE, ZombieMetaData.getInstance());

        //
        // THERMAL EXPANSION
        //
        if (DMLConstants.ModDependencies.isLoaded(DMLConstants.ModDependencies.THERMAL)) {
            store.put(EnumMobType.THERMAL_ELEMENTAL, ThermalElementalMetaData.getInstance());
        }

        //
        // TINKERS' CONSTRUCT
        //
        if (DMLConstants.ModDependencies.isLoaded(DMLConstants.ModDependencies.TINKERS)) {
            store.put(EnumMobType.TINKER_SLIME, TinkerSlimeMetaData.getInstance());
        }

        //
        // TWILIGHT FOREST
        //
        if (DMLConstants.ModDependencies.isLoaded(DMLConstants.ModDependencies.TWILIGHT)) {
            store.put(EnumMobType.TWILIGHT_FOREST, TwilightForestMetaData.getInstance());
            store.put(EnumMobType.TWILIGHT_SWAMP, TwilightSwampMetaData.getInstance());
            store.put(EnumMobType.TWILIGHT_DARKWOOD, TwilightDarkwoodMetaData.getInstance());
            store.put(EnumMobType.TWILIGHT_GLACIER, TwilightGlacierMetaData.getInstance());
        }

        //
        // MATTER OVERDRIVE
        //
        if (DMLConstants.ModDependencies.isLoaded(DMLConstants.ModDependencies.MO)) {
            store.put(EnumMobType.MO_ANDROID, MOAndroidMetaData.getInstance());
        }
    }

    public static MobMetaData getMetaData(EnumMobType mobType) {
        return store.getOrDefault(mobType, null);
    }

    public static boolean dataMobListContainsEntity(EnumMobType mobType, EntityLivingBase entity) {
        EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
        if (entityEntry == null)
            return false;

        ResourceLocation registryName = entityEntry.getRegistryName();
        if (registryName == null)
            return false;

        String name = registryName.toString();
        String[] mobList = DMLConfig.MOB_SETTINGS.getDataMobs(mobType);
        return Arrays.asList(mobList).contains(name);
    }
}
