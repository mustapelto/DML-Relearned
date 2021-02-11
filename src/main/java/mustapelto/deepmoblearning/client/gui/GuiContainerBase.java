package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.common.inventory.ContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.io.IOException;

public abstract class GuiContainerBase extends GuiContainer {
    private static final ResourceLocation PLAYER_INVENTORY_TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/player_inventory.png");

    protected final Minecraft mc;
    protected final TextureManager textureManager;
    protected final FontRenderer fontRenderer;
    protected final World world;
    protected final EntityPlayer player;

    // Some button clicks may require the button list to be rebuilt (e.g. loot fab's page change buttons)
    // which we can't do while iterating over it, so we change this variable and rebuild on next frame
    protected boolean buttonListNeedsRebuild = false;

    //
    // INIT
    //

    public GuiContainerBase(EntityPlayer player,
                            World world,
                            ContainerBase container,
                            int width,
                            int height) {
        super(container);

        mc = Minecraft.getMinecraft();
        this.textureManager = mc.getTextureManager();
        this.fontRenderer = mc.fontRenderer;
        this.player = player;
        this.world = world;

        xSize = width;
        ySize = height;
    }

    //
    // UPDATE
    //

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (buttonListNeedsRebuild)
            rebuildButtonList();
    }

    //
    // BUTTONS
    //

    /**
     * Initialize buttons. Called once when GUI is opened.
     */
    protected abstract void initButtons();


    /**
     * Rebuild list of buttons. Should be called whenever buttons are added/removed while GUI is open.
     */
    protected void rebuildButtonList() {
        buttonListNeedsRebuild = false;
        buttonList.clear();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttonList.forEach(button -> {
            if ((button instanceof ButtonBase) && button.mousePressed(mc, mouseX, mouseY))
                handleButtonPress((ButtonBase) button, mouseButton);
        });

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /** Handle clicks on custom buttons.
     *
     * @param button GUI button that was pressed.
     * @param mouseButton Mouse button that was pressed.
     */
    protected abstract void handleButtonPress(ButtonBase button, int mouseButton);

    //
    // DRAWING
    //

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * @param x Left edge of inventory area
     * @param y Top edge of inventory area
     */
    protected void drawPlayerInventory(int x, int y) {
        textureManager.bindTexture(PLAYER_INVENTORY_TEXTURE);
        drawTexturedModalRect(x, y, 0, 0, 176, 90);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        buttonList.forEach(button -> {
            if ((button instanceof ButtonBase) && button.enabled && button.isMouseOver()) {
                ImmutableList<String> tooltip = ((ButtonBase) button).getTooltip();
                if (!tooltip.isEmpty())
                    drawHoveringText(tooltip, mouseX - guiLeft, mouseY - guiTop);
            }
        });
    }
}
