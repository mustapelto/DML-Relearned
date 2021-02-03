package mustapelto.deepmoblearning.common.tiles;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

public enum RedstoneMode {
    ALWAYS_ON, HIGH_ON, HIGH_OFF, ALWAYS_OFF;

    private static final ImmutableList<RedstoneMode> values = ImmutableList.copyOf(values());

    public static boolean isActive(@Nonnull RedstoneMode mode, boolean redstonePowered) {
        return (mode == ALWAYS_ON) ||
                (mode == HIGH_ON && redstonePowered) ||
                (mode == HIGH_OFF && !redstonePowered);
    }

    public @Nonnull RedstoneMode next() {
        return values.get((values.indexOf(this) + 1) % values.size());
    }

    public @Nonnull RedstoneMode prev() {
        return values.get(Math.floorMod(values.indexOf(this) - 1, values.size()));
    }

    public int getIndex() {
        return values.indexOf(this);
    }

    public static RedstoneMode byIndex(int index) {
        if (index < 0 || index >= values.size())
            return ALWAYS_ON;

        return values.get(index);
    }
}
