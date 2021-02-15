package mustapelto.deepmoblearning.common.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ItemGlitchSword extends ItemSword {
    private static final ToolMaterial material = EnumHelper.addToolMaterial(
            "GLITCH_INFUSED_MATERIAL",
            3,
            2200,
            8.0f,
            9,
            15
    );

    public ItemGlitchSword() {
        super(material);
        String name = "glitch_infused_sword";
        setRegistryName(name);
        setUnlocalizedName(DMLConstants.ModInfo.ID + "." + name);
        setCreativeTab(DMLRelearned.creativeTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(I18n.format("deepmoblearning.glitch_sword.tooltip_1"));
        tooltip.add(I18n.format("deepmoblearning.glitch_sword.tooltip_2"));
        tooltip.add(TextFormatting.GOLD + I18n.format("deepmoblearning.glitch_sword.tooltip_3"));
        tooltip.add(I18n.format("deepmoblearning.glitch_sword.tooltip_4"));
        tooltip.add(I18n.format("deepmoblearning.glitch_sword.tooltip_5" + TextFormatting.RESET));

        String damage = TextFormatting.AQUA + String.valueOf(getPermanentWeaponDamage(stack)) + TextFormatting.RESET;
        tooltip.add(I18n.format("deepmoblearning.glitch_sword.tooltip_6", damage, DMLConstants.GlitchSword.DAMAGE_BONUS_MAX));
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, ItemStack repair) {
        return repair.getItem() instanceof ItemGlitchIngot;
    }

    @Override
    @Nonnull
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack stack) {
        AttributeModifier attackDamage = new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", material.getAttackDamage() + getPermanentWeaponDamage(stack), 0);
        AttributeModifier attackSpeed = new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4, 0);

        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), attackDamage);
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), attackSpeed);
        }

        return modifiers;
    }

    // Helper methods

    private static int getPermanentWeaponDamage(ItemStack stack) {
        return NBTHelper.getInteger(stack, "permDamage", 0);
    }

    private static void setPermanentWeaponDamage(ItemStack stack, int damage) {
        NBTHelper.setInteger(stack, "permDamage", damage);
    }

    public static boolean canIncreaseDamage(ItemStack stack) {
        return getPermanentWeaponDamage(stack) < DMLConstants.GlitchSword.DAMAGE_BONUS_MAX;
    }

    public static void increaseDamage(ItemStack stack, EntityPlayerMP player) {
        if (ThreadLocalRandom.current().nextInt(1, 100) < DMLConstants.GlitchSword.DAMAGE_INCREASE_CHANCE) {
            int currentDamage = getPermanentWeaponDamage(stack);
            int newDamage = currentDamage + DMLConstants.GlitchSword.DAMAGE_BONUS_INCREASE;
            if (newDamage > DMLConstants.GlitchSword.DAMAGE_BONUS_MAX)
                newDamage = DMLConstants.GlitchSword.DAMAGE_BONUS_MAX;

            setPermanentWeaponDamage(stack, newDamage);

            if (player == null)
                return;

            if (newDamage >= DMLConstants.GlitchSword.DAMAGE_BONUS_MAX)
                player.sendMessage(new TextComponentString(I18n.format("deepmoblearning.glitch_sword.max_damage_reached", stack.getDisplayName())));
            else
                player.sendMessage(new TextComponentString(I18n.format("deepmoblearning.glitch_sword.damage_increased", stack.getDisplayName())));
        }
    }
}
