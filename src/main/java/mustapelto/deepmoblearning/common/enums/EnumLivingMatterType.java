package mustapelto.deepmoblearning.common.enums;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.client.resources.I18n;

public enum EnumLivingMatterType {
    OVERWORLDIAN("overworldian", 10),
    HELLISH("hellish", 14),
    EXTRATERRESTRIAL("extraterrestrial", 20),
    TWILIGHT("twilight", 30, DMLConstants.ModDependencies.TWILIGHT);

    private final String name;
    private final int defaultXP;
    private final String modID;

    EnumLivingMatterType(String name, int defaultXP, String modID) {
        this.name = name;
        this.defaultXP = defaultXP;
        this.modID = modID;
    }

    EnumLivingMatterType(String name, int defaultXP) {
        this(name, defaultXP, DMLConstants.MINECRAFT);
    }

    public String getName() {
        return name;
    }

    public int getDefaultXP() {
        return defaultXP;
    }

    public String getModID() {
        return modID;
    }

    public boolean isVanilla() {
        return modID.equals(DMLConstants.MINECRAFT);
    }

    private String getDisplayName() {
        return I18n.format(String.format("deepmoblearning.mob_type.%s", name));
    }

    public String getDisplayNameFormatted() {
        return I18n.format(String.format("deepmoblearning.mob_type.%s.formatted", name), getDisplayName());
    }
}
