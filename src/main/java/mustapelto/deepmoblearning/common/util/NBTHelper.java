package mustapelto.deepmoblearning.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class NBTHelper {
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

    public static void setInt(ItemStack stack, String key, int value) {
        getTag(stack).setInteger(key, value);
    }

    public static int getInt(ItemStack stack, String key, int defaultValue) {
        return hasTag(stack) ? getTag(stack).getInteger(key) : defaultValue;
    }

    public static void setList(ItemStack stack, String key, NBTTagList list) {
        getTag(stack).setTag(key, list);
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
}
