package mustapelto.deepmoblearning.common.mobdata;

import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class MobMetaData {
    private final String name;
    private final String pluralName;
    private final int numberOfHearts;
    private final int interfaceScale;
    private final int interfaceOffsetX;
    private final int interfaceOffsetY;
    private final EnumLivingMatterType livingMatterType;
    private final String[] mobTrivia;
    private final String extraTooltip;

    public MobMetaData(String name, String pluralName, int numberOfHearts, int interfaceScale, int interfaceOffsetX, int interfaceOffsetY,
                       EnumLivingMatterType livingMatterType, String[] mobTrivia, String extraTooltip) {
        this.name = name;
        this.pluralName = pluralName;
        this.numberOfHearts = numberOfHearts;
        this.interfaceScale = interfaceScale;
        this.interfaceOffsetX = interfaceOffsetX;
        this.interfaceOffsetY = interfaceOffsetY;
        this.livingMatterType = livingMatterType;
        this.mobTrivia = mobTrivia;
        this.extraTooltip = extraTooltip;
    }

    public MobMetaData(String name, int numberOfHearts, int interfaceScale, int interfaceOffsetX, int interfaceOffsetY,
                       EnumLivingMatterType livingMatterType, String[] mobTrivia, String extraTooltip) {
        this(name,
                name + "s",
                numberOfHearts,
                interfaceScale,
                interfaceOffsetX,
                interfaceOffsetY,
                livingMatterType,
                mobTrivia,
                extraTooltip);
    }

    public MobMetaData(String name, String pluralName, int numberOfHearts, int interfaceScale, int interfaceOffsetX, int interfaceOffsetY,
                       EnumLivingMatterType livingMatterType, String[] mobTrivia) {
        this(name,
                pluralName,
                numberOfHearts,
                interfaceScale,
                interfaceOffsetX,
                interfaceOffsetY,
                livingMatterType,
                mobTrivia,
                "");
    }

    public MobMetaData(String name, int numberOfHearts, int interfaceScale, int interfaceOffsetX, int interfaceOffsetY,
                       EnumLivingMatterType livingMatterType, String[] mobTrivia) {
        this(name,
                name + "s",
                numberOfHearts,
                interfaceScale,
                interfaceOffsetX,
                interfaceOffsetY,
                livingMatterType,
                mobTrivia,
                "");
    }



    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
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

    public String[] getMobTrivia() {
        return mobTrivia;
    }

    public String getExtraTooltip() {
        return extraTooltip;
    }

    public abstract Entity getEntity(World world);

    public abstract static class MobMetaDataExtra extends MobMetaData {
        private final int extraInterfaceOffsetX;
        private final int extraInterfaceOffsetY;

        public MobMetaDataExtra(String name, String pluralName, int numberOfHearts, int interfaceScale, int interfaceOffsetX,
                                int interfaceOffsetY, EnumLivingMatterType livingMatterType, String[] mobTrivia,
                                int extraInterfaceOffsetX, int extraInterfaceOffsetY, String extraTooltip) {
            super(name,
                    pluralName,
                    numberOfHearts,
                    interfaceScale,
                    interfaceOffsetX,
                    interfaceOffsetY,
                    livingMatterType,
                    mobTrivia,
                    extraTooltip);
            this.extraInterfaceOffsetX = extraInterfaceOffsetX;
            this.extraInterfaceOffsetY = extraInterfaceOffsetY;
        }

        public MobMetaDataExtra(String name, int numberOfHearts, int interfaceScale, int interfaceOffsetX, int interfaceOffsetY,
                                EnumLivingMatterType livingMatterType, String[] mobTrivia, int extraInterfaceOffsetX, int extraInterfaceOffsetY,
                                String extraTooltip) {
            this(name,
                    name + "s",
                    numberOfHearts,
                    interfaceScale,
                    interfaceOffsetX,
                    interfaceOffsetY,
                    livingMatterType,
                    mobTrivia,
                    extraInterfaceOffsetX,
                    extraInterfaceOffsetY,
                    extraTooltip);
        }

        public int getExtraInterfaceOffsetX() {
            return extraInterfaceOffsetX;
        }

        public int getExtraInterfaceOffsetY() {
            return extraInterfaceOffsetY;
        }

        public abstract Entity getEntityExtra(World world);
    }
}