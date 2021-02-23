package mustapelto.deepmoblearning.common.util;

import com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class MathHelper {
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

    public static <T> Optional<T> getWeightedRandom(ImmutableList<WeightedItem<T>> items) {
        int weightSum = items.stream()
                .map(WeightedItem::getWeight)
                .mapToInt(Integer::intValue)
                .sum();

        int random = ThreadLocalRandom.current().nextInt(weightSum);
        for (WeightedItem<T> item : items) {
            int itemWeight = item.getWeight();
            if (random < itemWeight)
                return Optional.of(item.getValue());

            random -= itemWeight;
        }

        return Optional.empty(); // This should never happen!
    }
}
