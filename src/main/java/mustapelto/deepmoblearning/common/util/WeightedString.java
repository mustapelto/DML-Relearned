package mustapelto.deepmoblearning.common.util;

import net.minecraft.util.WeightedRandom;

public class WeightedString extends WeightedRandom.Item {
    private final String value;

    public WeightedString(String value, int weight) {
        super(weight);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
