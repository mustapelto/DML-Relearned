package mustapelto.deepmoblearning.client.util;

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

    public static String getSneakKeyName() {
        return Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName();
    }

    public static String getAttackKeyName() {
        return Minecraft.getMinecraft().gameSettings.keyBindAttack.getDisplayName();
    }

    public static String getSprintKeyName() {
        return Minecraft.getMinecraft().gameSettings.keyBindSprint.getDisplayName();
    }

    public static String getUseKeyName() {
        return Minecraft.getMinecraft().gameSettings.keyBindUseItem.getDisplayName();
    }
}
