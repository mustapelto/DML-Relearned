package mustapelto.deepmoblearning.common.mobdata;

import mustapelto.deepmoblearning.DMLConstants;

public enum EnumMobType {
    // Vanilla
    BLAZE("blaze"),
    CREEPER("creeper"),
    ENDER_DRAGON("dragon"),
    ENDERMAN("enderman"),
    GHAST("ghast"),
    GUARDIAN("guardian"),
    SHULKER("shulker"),
    SKELETON("skeleton"),
    SLIME("slime"),
    SPIDER("spider"),
    WITCH("witch"),
    WITHER("wither"),
    WITHER_SKELETON("wither_skeleton"),
    ZOMBIE("zombie"),
    // Thermal Expansion
    THERMAL_ELEMENTAL("thermal_elemental", DMLConstants.ModDependencies.THERMAL),
    // Twilight Forest
    TWILIGHT_FOREST("twilight_forest", DMLConstants.ModDependencies.TWILIGHT),
    TWILIGHT_SWAMP("twilight_swamp", DMLConstants.ModDependencies.TWILIGHT),
    TWILIGHT_DARKWOOD("twilight_darkwood", DMLConstants.ModDependencies.TWILIGHT),
    TWILIGHT_GLACIER("twilight_glacier", DMLConstants.ModDependencies.TWILIGHT),
    // Tinkers' Construct
    TINKER_SLIME("tinker_slime", DMLConstants.ModDependencies.TINKERS),
    // Matter Overdrive
    MO_ANDROID("mo_android", DMLConstants.ModDependencies.MO);

    private final String name;
    private final String modID;

    EnumMobType(String name, String modID) {
        this.name = name;
        this.modID = modID;
    }

    EnumMobType(String name) {
        this(name, DMLConstants.VANILLA);
    }

    public String getName() {
        return name;
    }

    public String getModID() {
        return modID;
    }

    public boolean isVanilla() {
        return modID.equals(DMLConstants.VANILLA);
    }
}
