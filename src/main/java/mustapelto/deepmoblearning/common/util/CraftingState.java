package mustapelto.deepmoblearning.common.util;

import net.minecraft.util.IStringSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Possible crafting states for machines
 * - idle (= no input)
 * - running
 * - error (= has input but can't start for some reason)
 *
 * gets saved into machine NBT by integer index
 */
public enum CraftingState implements IStringSerializable {
    IDLE(0, "idle"),
    RUNNING(1, "running"),
    ERROR(2, "error");

    private final int index;
    private final String name;
    private static final Map<Integer, CraftingState> indexMap = new HashMap<>();

    CraftingState(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    public static CraftingState byIndex(int index) {
        return indexMap.getOrDefault(index, CraftingState.IDLE);
    }

    static {
        for (CraftingState state : CraftingState.values()) {
            indexMap.put(state.index, state);
        }
    }
}
