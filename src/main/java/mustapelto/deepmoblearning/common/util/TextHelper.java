package mustapelto.deepmoblearning.common.util;

import net.minecraft.util.text.TextFormatting;

public class TextHelper {
    public static String getFormattedString(TextFormatting pre, String string, TextFormatting post) {
        return TextFormatting.RESET + "" + pre + string + TextFormatting.RESET + "" + post;
    }
}
