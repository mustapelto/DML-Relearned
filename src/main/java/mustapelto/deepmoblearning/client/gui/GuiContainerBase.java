package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.common.inventory.ContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class GuiContainerBase extends GuiContainer {
    protected final FontRenderer fontRenderer;
    protected final World world;
    protected final EntityPlayer player;

    public GuiContainerBase(EntityPlayer player,
                            World world,
                            ContainerBase container,
                            int width,
                            int height) {
        super(container);

        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        this.player = player;
        this.world = world;

        xSize = width;
        ySize = height;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
}
