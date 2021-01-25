package mustapelto.deepmoblearning.common.util;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class KeyboardHelper {
    public static boolean isHoldingSneakKey() {
        int keyCode = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
        return isHoldingKey(keyCode);
    }

    public static boolean isHoldingSprintKey() {
        int keyCode = Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode();
        return isHoldingKey(keyCode);
    }

    private static boolean isHoldingKey(int keyCode) {
        return Keyboard.isCreated() && Keyboard.isKeyDown(keyCode);
    }
}
