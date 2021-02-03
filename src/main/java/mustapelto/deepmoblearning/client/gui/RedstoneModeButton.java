package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.common.tiles.RedstoneMode;
import mustapelto.deepmoblearning.common.tiles.TileEntityRedstoneControlled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;

public class RedstoneModeButton {
    private final GuiContainer gui;
    private final TileEntityRedstoneControlled tileEntity;
    private final int left;
    private final int top;
    private final TextureManager textureManager;

    public RedstoneModeButton(GuiContainer gui, TileEntityRedstoneControlled tileEntity, int left, int top) {
        this.gui = gui;
        this.tileEntity = tileEntity;
        this.left = left;
        this.top = top;
        textureManager = Minecraft.getMinecraft().getTextureManager();
    }

    public void drawButton(int mouseX, int mouseY) {
        textureManager.bindTexture(GuiRegistry.BUTTONS.REDSTONE_BUTTON);

        int textureX = 18 * tileEntity.getRedstoneMode().getIndex();
        int textureY = isHovered(mouseX, mouseY) ? 18 : 0;

        gui.drawTexturedModalRect(gui.getGuiLeft() + left, gui.getGuiTop() + top, textureX, textureY, 18, 18);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (mouseX >= left) && (mouseX <= (left + 18)) && (mouseY >= top) && (mouseY <= (top + 18));
    }

    public void click(int mouseButton) {
        RedstoneMode mode = tileEntity.getRedstoneMode();
        if (mouseButton == 0)
            tileEntity.setRedstoneMode(mode.next());
        if (mouseButton == 1)
            tileEntity.setRedstoneMode(mode.prev());
    }

    public ArrayList<String> getTooltip() {
        ArrayList<String> tooltip = new ArrayList<>();
        switch (tileEntity.getRedstoneMode()) {
            case ALWAYS_ON:
                tooltip.add(I18n.format("deepmoblearning.redstone_mode.always_on"));
                break;
            case HIGH_ON:
                tooltip.add(I18n.format("deepmoblearning.redstone_mode.high_on"));
                break;
            case HIGH_OFF:
                tooltip.add(I18n.format("deepmoblearning.redstone_mode.high_off"));
                break;
            case ALWAYS_OFF:
                tooltip.add(I18n.format("deepmoblearning.redstone_mode.always_off"));
                break;
        }
        return tooltip;
    }
}
