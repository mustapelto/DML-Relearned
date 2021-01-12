package mustapelto.deepmoblearning.common.mobdata;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Arrays;

public abstract class MobMetaData {
    private final String baseTranslateKey;
    private final int numberOfHearts;
    private final int interfaceScale;
    private final int interfaceOffsetX;
    private final int interfaceOffsetY;
    private final EnumLivingMatterType livingMatterType;
    private final int defaultRFCost;
    private final String[] defaultDataMobs;

    public MobMetaData(String name,
                       int numberOfHearts,
                       int interfaceScale,
                       int interfaceOffsetX,
                       int interfaceOffsetY,
                       EnumLivingMatterType livingMatterType,
                       int defaultRFCost,
                       String[] defaultDataMobs) {
        baseTranslateKey = String.format("deepmoblearning.mob_meta.%s", name);

        this.numberOfHearts = numberOfHearts;
        this.interfaceScale = interfaceScale;
        this.interfaceOffsetX = interfaceOffsetX;
        this.interfaceOffsetY = interfaceOffsetY;
        this.livingMatterType = livingMatterType;
        this.defaultRFCost = defaultRFCost;
        this.defaultDataMobs = defaultDataMobs;
    }

    private String translationOrEmpty(String baseKey, String subKey) {
        return I18n.hasKey(baseKey + subKey) ? I18n.format(baseKey + subKey) : "";
    }

    public String getDisplayName() {
        return translationOrEmpty(baseTranslateKey, ".display_name");
    }

    public String getDisplayNamePlural() {
        String displayNamePlural = translationOrEmpty(baseTranslateKey, ".display_name_plural");
        return !displayNamePlural.equals("") ? displayNamePlural : getDisplayName() + "s";
    }

    public int getNumberOfHearts() {
        return numberOfHearts;
    }

    public int getInterfaceScale() {
        return interfaceScale;
    }

    public int getInterfaceOffsetX() {
        return interfaceOffsetX;
    }

    public int getInterfaceOffsetY() {
        return interfaceOffsetY;
    }

    public EnumLivingMatterType getLivingMatterType() {
        return livingMatterType;
    }

    public String[] getTrivia() {
        return translationOrEmpty(baseTranslateKey, ".trivia").split("\n");
    }

    public String getExtraTooltip() {
        return translationOrEmpty(baseTranslateKey, ".extra_tooltip");
    }

    public int getDefaultRFCost() {
        return defaultRFCost;
    }

    public String[] getDefaultDataMobs() {
        return defaultDataMobs;
    }

    public abstract Entity getEntity(World world);

    public abstract static class MobMetaDataExtra extends MobMetaData {
        private final int extraInterfaceOffsetX;
        private final int extraInterfaceOffsetY;

        public MobMetaDataExtra(String name,
                                int numberOfHearts,
                                int interfaceScale,
                                int interfaceOffsetX,
                                int interfaceOffsetY,
                                EnumLivingMatterType livingMatterType,
                                int defaultRFCost,
                                String[] defaultDataMobs,
                                int extraInterfaceOffsetX,
                                int extraInterfaceOffsetY) {
            super(name,
                    numberOfHearts,
                    interfaceScale,
                    interfaceOffsetX,
                    interfaceOffsetY,
                    livingMatterType,
                    defaultRFCost,
                    defaultDataMobs
            );
            this.extraInterfaceOffsetX = extraInterfaceOffsetX;
            this.extraInterfaceOffsetY = extraInterfaceOffsetY;
        }

        public abstract Entity getEntityExtra(World world);

        public int getExtraInterfaceOffsetX() {
            return extraInterfaceOffsetX;
        }

        public int getExtraInterfaceOffsetY() {
            return extraInterfaceOffsetY;
        }
    }
}