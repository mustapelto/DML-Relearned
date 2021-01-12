package mustapelto.deepmoblearning.common.util;

public class Rect {
    public final int LEFT;
    public final int TOP;
    public final int RIGHT;
    public final int BOTTOM;
    public final int WIDTH;
    public final int HEIGHT;

    public Rect(int left, int top, int width, int height) {
        LEFT = left;
        TOP = top;
        WIDTH = width;
        HEIGHT = height;
        RIGHT = left + width;
        BOTTOM = top + height;
    }
}
