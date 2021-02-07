package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ButtonBase extends GuiButton {
    protected final ResourceLocation texture;
    protected ImmutableList<String> tooltip;

    public ButtonBase(int buttonId, int x, int y, int width, int height, @Nullable ResourceLocation texture) {
        super(buttonId, x, y, width, height, "");
        this.texture = texture;
    }

    protected boolean isHovered(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY <= y + width);
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return hovered ? 1 : 0;
    }

    protected int getState() {
        return 0;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        this.hovered = isHovered(mouseX, mouseY);
        int hoverState = getHoverState(hovered);

        if (visible && texture != null) {
            mc.getTextureManager().bindTexture(texture);
            GlStateManager.color(1f, 1f, 1f, 1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            drawTexturedModalRect(x, y, getState() * width, hoverState * height, width, height);
        }
    }

    public ImmutableList<String> getTooltip() {
        return tooltip;
    }

    protected abstract void buildTooltip();

    public abstract void handleClick(int mouseButton);
}
