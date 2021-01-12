package mustapelto.deepmoblearning.common.enums;

import mustapelto.deepmoblearning.DMLConstants;

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
}
