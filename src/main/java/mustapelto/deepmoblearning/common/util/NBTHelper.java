package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class NBTHelper {
    //
    // ItemStack methods
    //

    public static boolean hasTag(ItemStack stack) {
        return stack.hasTagCompound();
    }

    public static NBTTagCompound getTag(ItemStack stack) {
        if (!hasTag(stack)) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public static boolean hasKey(ItemStack stack, String key) {
        return hasTag(stack) && getTag(stack).hasKey(key);
    }

    public static void setInteger(ItemStack stack, String key, int value) {
        getTag(stack).setInteger(key, value);
    }

    public static int getInteger(ItemStack stack, String key, int defaultValue) {
        return hasTag(stack) ? getInteger(getTag(stack), key, defaultValue) : defaultValue;
    }

    public static void setString(ItemStack stack, String key, String value) {
        getTag(stack).setString(key, value);
    }

    public static String getString(ItemStack stack, String key, String defaultValue) {
        return hasTag(stack) ? getString(getTag(stack), key, defaultValue) : defaultValue;
    }

    public static NBTTagList getCompoundList(ItemStack stack, String key) {
        return hasTag(stack) ? getTag(stack).getTagList(key, Constants.NBT.TAG_COMPOUND) : null;
    }

    public static void removeKey(ItemStack stack, String key) {
        if (hasKey(stack, key))
            getTag(stack).removeTag(key);
        if (getTag(stack).hasNoTags())
            stack.setTagCompound(null);
    }

    public static void setVersion(ItemStack stack) {
        setVersion(getTag(stack));
    }

    public static boolean isLegacyNBT(ItemStack stack) {
        return hasTag(stack) && isLegacyNBT(getTag(stack));
    }

    //
    // NBTTagCompound
    //

    public static final String DMLR_VERSION = "dmlrVersion";

    public static int getInteger(NBTTagCompound compound, String key, int defaultValue) {
        return compound.hasKey(key, Constants.NBT.TAG_INT) ? compound.getInteger(key) : defaultValue;
    }

    public static boolean getBoolean(NBTTagCompound compound, String key, boolean defaultValue) {
        return compound.hasKey(key, Constants.NBT.TAG_BYTE) ? compound.getBoolean(key) : defaultValue;
    }

    public static String getString(NBTTagCompound compound, String key, String defaultValue) {
        return compound.hasKey(key, Constants.NBT.TAG_STRING) ? compound.getString(key) : defaultValue;
    }

    public static boolean isLegacyNBT(NBTTagCompound compound) {
        return !compound.hasKey(DMLR_VERSION);
    }

    public static void setVersion(NBTTagCompound compound) {
        compound.setString(DMLR_VERSION, DMLConstants.ModInfo.VERSION);
    }
}
