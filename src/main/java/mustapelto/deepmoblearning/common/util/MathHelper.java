package mustapelto.deepmoblearning.common.util;

public class MathHelper {
    /**
     * Integer division, but result is always rounded up
     * @param a dividend
     * @param b divisor
     * @return division result, rounded up to nearest integer
     */
    public static int divideAndRoundUp(int a, int b) {
        return a / b + ((a % b == 0) ? 0 : 1);
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
