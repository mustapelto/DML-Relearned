package mustapelto.deepmoblearning.common.util;

public class MathHelper {
    public static int DivideAndRoundUp(int a, int b) {
        return a / b + ((a % b == 0) ? 0 : 1);
    }

    public static int Clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
