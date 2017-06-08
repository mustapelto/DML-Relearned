package mustapelto.deepmoblearning.common.enums;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData;
import mustapelto.deepmoblearning.common.mobdata.MobMetaData.MobMetaDataExtra;
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

public enum EnumMobType {
    BLAZE("blaze", 256),
    CREEPER("creeper", 80),
    ENDER_DRAGON("dragon", 2560),
    ENDERMAN("enderman", 512),
    GHAST("ghast", 372),
    GUARDIAN("guardian", 340),
    SHULKER("shulker", 256),
    SKELETON("skeleton", 80),
    SLIME("slime", 150),
    SPIDER("spider", 80),
    WITCH("witch", 120),
    WITHER("wither", 2048),
    WITHERSKELETON("wither_skeleton", 880),
    ZOMBIE("zombie", 80),
    THERMAL("thermal_elemental", 256, DMLConstants.ModDependencies.THERMAL),
    TWILIGHTFOREST("twilight_forest", 256, DMLConstants.ModDependencies.TWILIGHT),
    TWILIGHTSWAMP("twilight_swamp", 256, DMLConstants.ModDependencies.TWILIGHT),
    TWILIGHTDARKWOOD("twilight_darkwood", 256, DMLConstants.ModDependencies.TWILIGHT),
    TWILIGHTGLACIER("twilight_glacier", 256, DMLConstants.ModDependencies.TWILIGHT),
    TINKERSLIME("tinker_slime", 256, DMLConstants.ModDependencies.TINKERS),
    MO_ANDROID("mo_android", 256, DMLConstants.ModDependencies.MO);

    private final String name;
    private final int defaultRFCost;
    private final String modID;

    EnumMobType(String name, int defaultRFCost, String modID) {
        this.name = name;
        this.defaultRFCost = defaultRFCost;
        this.modID = modID;
    }

    EnumMobType(String name, int defaultRFCost) {
        this(name, defaultRFCost, DMLConstants.VANILLA);
    }

    public String getName() {
        return name;
    }

    public int getDefaultRFCost() {
        return defaultRFCost;
    }

    public boolean isVanilla() {
        return modID.equals(DMLConstants.VANILLA);
    }

    public String getModID() {
        return modID;
    }

    public int getSimulationTickCost() {
        return DMLConfig.getSimulationTickCost(this);
    }

    public EnumLivingMatterType getLivingMatterType() {
        return store.containsKey(this) ? store.get(this).getLivingMatterType() : null;
    }

    @Nullable
    public Entity getEntity(World world) {
        return store.containsKey(this) ? store.get(this).getEntity(world) : null;
    }

    @Nullable
    public Entity getEntityExtra(World world) {
        MobMetaData data = store.getOrDefault(this, null);
        return data instanceof MobMetaDataExtra ? ((MobMetaDataExtra) data).getEntityExtra(world) : null;
    }

    public String getExtraTooltip() {
        MobMetaData data = store.getOrDefault(this, null);
        return data instanceof MobMetaDataExtra ? ((MobMetaDataExtra) data).getExtraTooltip() : "";
    }

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
}
