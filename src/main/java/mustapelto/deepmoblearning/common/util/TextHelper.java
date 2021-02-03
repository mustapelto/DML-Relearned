package mustapelto.deepmoblearning.common.util;

import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class TextHelper {
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
}
