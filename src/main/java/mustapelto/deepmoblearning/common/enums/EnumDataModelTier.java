package mustapelto.deepmoblearning.common.enums;

import net.minecraft.client.resources.I18n;

import java.util.HashMap;

public enum EnumDataModelTier {
    FAULTY(0, 1, 0),
    BASIC(1, 4, 6),
    ADVANCED(2, 10, 12),
    SUPERIOR(3, 18, 30),
    SELF_AWARE(4, 0, 50);

    private final int level;
    private final int killMultiplierDefault;
    private final int killsRequiredDefault;

    private static final HashMap<Integer, EnumDataModelTier> byLevel = new HashMap<>();

    static {
        for (EnumDataModelTier e : values()) {
            byLevel.put(e.level, e);
        }
    }

    EnumDataModelTier(int level, int killMultiplierDefault, int killsRequiredDefault) {
        this.level = level;
        this.killMultiplierDefault = killMultiplierDefault;
        this.killsRequiredDefault = killsRequiredDefault;
    }

    public int getLevel() {
        return level;
    }

    public int getKillMultiplierDefault() {
        return killMultiplierDefault;
    }

    public int getKillsRequiredDefault() {
        return killsRequiredDefault;
    }

    public static EnumDataModelTier fromLevel(int level) {
        return byLevel.getOrDefault(level, null);
    }

    public String getDisplayName() {
        return I18n.format(String.format("deepmoblearning.data_model.tier.%d", level));
    }

    public String getDisplayNameFormatted() {
        return I18n.format(String.format("deepmoblearning.data_model.tier.%d.formatted", level), getDisplayName());
    }
}
