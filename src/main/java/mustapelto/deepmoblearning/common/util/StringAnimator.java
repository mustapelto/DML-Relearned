package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.client.gui.SimulationChamberGui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class StringAnimator {
    private final LinkedHashMap<SimulationChamberGui.AnimatedString, AnimatedString> strings;
    private final List<SimulationChamberGui.AnimatedString> keys;
    private int currentStringIndex;
    private float totalDuration;
    private boolean finished;

    public StringAnimator() {
        strings = new LinkedHashMap<>();
        keys = new ArrayList<>();
        finished = false;
        currentStringIndex = 0;
        totalDuration = 0;
    }

    public void reset() {
        currentStringIndex = 0;
        finished = false;
        strings.values().forEach(AnimatedString::reset);
    }

    public void addString(SimulationChamberGui.AnimatedString key, String string) {
        addString(key, string, 1, false);
    }

    public void addString(SimulationChamberGui.AnimatedString key, String string, float speed, boolean loop) {
        AnimatedString newString = new AnimatedString(string, speed, loop);
        strings.put(key, newString);
        keys.add(key);
        totalDuration += newString.getDuration();
    }

    public void setString(SimulationChamberGui.AnimatedString key, String string) {
        AnimatedString oldString = strings.get(key);
        AnimatedString newString = new AnimatedString(string, oldString.getSpeed(), oldString.isLoop());
        strings.replace(key, newString);
        totalDuration -= oldString.getDuration();
        totalDuration += newString.getDuration();
    }

    public void advance(float amount) {
        if (finished)
            return;

        AnimatedString currentString = getByIndex(currentStringIndex);
        if (currentStringIndex == strings.size() && currentString.isFinished())
            return;

        while (amount > 0) {
            amount = currentString.advance(amount);
            if (currentString.isFinished()) {
                if (currentStringIndex < strings.size() - 1) {
                    currentStringIndex++;
                    currentString = getByIndex(currentStringIndex);
                }
                else {
                    finished = true;
                    break;
                }
            }
        }
    }

    public List<String> getCurrentStrings() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i <= currentStringIndex; i++) {
            result.add(strings.get(keys.get(i)).getString());
        }
        return result;
    }

    public void goToPosition(float position) {
        reset();
        advance(position);
    }

    public void goToRelativePosition(float relativePosition) {
        relativePosition = MathHelper.Clamp(relativePosition, 0, 1);
        goToPosition(relativePosition * totalDuration);
    }

    private AnimatedString getByIndex(int index) {
        return strings.get(keys.get(index));
    }
}
