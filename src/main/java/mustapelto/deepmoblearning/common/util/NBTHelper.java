package mustapelto.deepmoblearning.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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

    public static void setInt(ItemStack stack, String key, int value) {
        getTag(stack).setInteger(key, value);
    }

    public static int getInt(ItemStack stack, String key, int defaultValue) {
        return hasTag(stack) ? getTag(stack).getInteger(key) : defaultValue;
    }
}
