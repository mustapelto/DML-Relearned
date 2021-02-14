package mustapelto.deepmoblearning.common.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class StringHelper {
    public static String getFormattedString(String string, TextFormatting formatting) {
        return (formatting != null) ?
                formatting + string + TextFormatting.RESET :
                string;
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

    public static ImmutableList<String> replaceInList(ImmutableList<String> original, String toReplace, String replaceWith) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        for (String entry : original) {
            String newEntry = entry;
            if (newEntry.equals(toReplace))
                newEntry = replaceWith;
            builder.add(newEntry);
        }

        return builder.build();
    }

    public static String uppercaseFirst(String original) {
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
