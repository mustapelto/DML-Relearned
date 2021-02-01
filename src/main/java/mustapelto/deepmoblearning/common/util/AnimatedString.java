package mustapelto.deepmoblearning.common.util;

public class AnimatedString {
    private final String string;
    private final int ticksPerLetter;
    private int currentTicks = 0;
    private int currentLastLetter = 0;
    private boolean finished = false;

    public AnimatedString(String string, int ticksPerLetter) {
        this.string = string;
        this.ticksPerLetter = ticksPerLetter;
    }

    public void tick() {
        if (finished)
            return;

        currentTicks++;
        if (currentTicks >= ticksPerLetter) {
            currentTicks = 0;
            currentLastLetter++;
            if (currentLastLetter >= string.length() - 1)
                finished = true;
        }
    }

    public String getString() {
        return string.substring(0, currentLastLetter);
    }

    public boolean isFinished() {
        return finished;
    }

    public void reset() {
        currentTicks = 0;
        currentLastLetter = 0;
        finished = false;
    }
}
