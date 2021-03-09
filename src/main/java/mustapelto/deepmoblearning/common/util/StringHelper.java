package mustapelto.deepmoblearning.common.util;

import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class StringHelper {
    public static String getFormattedString(String string, TextFormatting formatting) {
        return formatting + string + TextFormatting.RESET;
    }

    public static String getFormattedString(TextFormatting pre, String string, TextFormatting post) {
        return TextFormatting.RESET + "" + pre + string + TextFormatting.RESET + "" + post;
    }

    public static String getDashedLine(int length) {
        return StringUtils.repeat('-', length);
    }

    public static String pad(String original, int targetLength) {
        int padLength = targetLength - original.length();
        if (padLength <= 0)
            return original;

        int leftPad = padLength / 2;
        String leftPadded = StringUtils.leftPad(original, original.length() + leftPad);
        return StringUtils.rightPad(leftPadded, targetLength);
    }

    public static String uppercaseFirst(String original) {
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static String toRegistryName(String domain, String path) {
        return String.format("%s:%s", domain, path);
    }
}
