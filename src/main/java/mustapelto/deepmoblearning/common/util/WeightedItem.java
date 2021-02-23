package mustapelto.deepmoblearning.common.util;

public class WeightedItem<T> {
    private final T value;
    private final int weight;

    public WeightedItem(T value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    public T getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }
}
