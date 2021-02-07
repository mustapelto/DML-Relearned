package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.common.tiles.TileEntityMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.IOException;

public abstract class GuiMachine extends GuiContainerBase {
    protected final TileEntityMachine tileEntity;

    public GuiMachine(TileEntityMachine tileEntity, EntityPlayer player, World world, int width, int height) {
        super(player, world, tileEntity.getContainer(player.inventory), width, height);
        this.tileEntity = tileEntity;
        this.tileEntity.setGuiOpen(true);
    }

    @Override
    public void onGuiClosed() {
        tileEntity.setGuiOpen(false);
        super.onGuiClosed();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (GuiButton button : buttonList) {
            if (button instanceof ButtonBase && button.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
                ((ButtonBase) button).handleClick(mouseButton);
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void drawButtonTooltip(int mouseX, int mouseY) {
        for (GuiButton button : buttonList) {
            if (button instanceof ButtonBase && button.isMouseOver()) {
                ImmutableList<String> tooltip = ((ButtonBase) button).getTooltip();
                drawHoveringText(tooltip, mouseX, mouseY);
            }
        }
    }
}
