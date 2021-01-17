package mustapelto.deepmoblearning.common.util;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class KeyboardHelper {
    public static String getSneakDisplayName() {
        return Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName();
    }

    public static String getUseDisplayName() {
        return Minecraft.getMinecraft().gameSettings.keyBindUseItem.getDisplayName();
    }

    public static String getSprintDisplayName() {
        return Minecraft.getMinecraft().gameSettings.keyBindSprint.getDisplayName();
    }

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
