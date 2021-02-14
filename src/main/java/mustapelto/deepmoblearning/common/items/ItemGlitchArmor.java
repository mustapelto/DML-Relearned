package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ItemGlitchArmor extends ItemArmor {
    private static final ArmorMaterial material = EnumHelper.addArmorMaterial(
            "GLITCH_INFUSED_MATERIAL",
            DMLConstants.ModInfo.ID + ":" + "glitch_infused",
            120,
            new int[]{3, 8, 6, 3},
            15,
            null,
            3.0f
    );

    public ItemGlitchArmor(String name, EntityEquipmentSlot slot) {
        super(material, 0, slot);
        setRegistryName(name);
        setUnlocalizedName(DMLConstants.ModInfo.ID + "." + name);
        setCreativeTab(DMLRelearned.creativeTab);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        String pristineChance = DMLConfig.GENERAL_SETTINGS.GLITCH_ARMOR_PRISTINE_CHANCE + "%";
        String pristineCount = String.valueOf(DMLConfig.GENERAL_SETTINGS.GLITCH_ARMOR_PRISTINE_COUNT);

        tooltip.add(TextFormatting.RESET + I18n.format("deepmoblearning.glitch_armor.tooltip_1"));
        tooltip.add(I18n.format("deepmoblearning.glitch_armor.tooltip_2"));
        tooltip.add(TextFormatting.GOLD + I18n.format("deepmoblearning.glitch_armor.tooltip_3", pristineChance, pristineCount));
        tooltip.add(I18n.format("deepmoblearning.glitch_armor.tooltip_4") + TextFormatting.RESET);

        if (DMLConfig.GENERAL_SETTINGS.GLITCH_CREATIVE_FLIGHT_ENABLED)
            tooltip.add(TextFormatting.GOLD + I18n.format("deepmoblearning.glitch_armor.tooltip_5") + TextFormatting.RESET);
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, ItemStack repair) {
        return repair.getItem() instanceof ItemGlitchIngot;
    }

    // Helper methods

    public static boolean isSetEquipped(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemGlitchArmor &&
                player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemGlitchArmor &&
                player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemGlitchArmor &&
                player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemGlitchArmor;
    }

    public static void dropPristineMatter(World world, BlockPos position, ItemStack dataModel) {
        if (ThreadLocalRandom.current().nextInt(1, 100) <= DMLConfig.GENERAL_SETTINGS.GLITCH_ARMOR_PRISTINE_CHANCE) {
            MetadataDataModel metadata = DataModelHelper.getDataModelMetadata(dataModel);
            if (metadata.isInvalid())
                return;
            EntityItem drop = new EntityItem(world, position.getX(), position.getY(), position.getZ(), metadata.getPristineMatter());
            drop.setDefaultPickupDelay();
            world.spawnEntity(drop);
        }
    }

    // Subclasses

    public static class ItemGlitchHelmet extends ItemGlitchArmor {
        public ItemGlitchHelmet() {
            super("glitch_infused_helmet", EntityEquipmentSlot.HEAD);
        }
    }

    public static class ItemGlitchChestplate extends ItemGlitchArmor {
        public ItemGlitchChestplate() {
            super("glitch_infused_chestplate", EntityEquipmentSlot.CHEST);
        }
    }

    public static class ItemGlitchLeggings extends ItemGlitchArmor {
        public ItemGlitchLeggings() {
            super("glitch_infused_leggings", EntityEquipmentSlot.LEGS);
        }
    }

    public static class ItemGlitchBoots extends ItemGlitchArmor {
        public ItemGlitchBoots() {
            super("glitch_infused_boots", EntityEquipmentSlot.FEET);
        }
    }
}
