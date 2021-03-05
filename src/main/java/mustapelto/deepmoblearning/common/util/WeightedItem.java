package mustapelto.deepmoblearning.common.util;

import net.minecraft.util.WeightedRandom;

public class WeightedItem<T> extends WeightedRandom.Item {
    private final T value;

    public WeightedItem(T value, int weight) {
        super(weight);
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
