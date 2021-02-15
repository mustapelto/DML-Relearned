package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonRedstoneMode;
import mustapelto.deepmoblearning.common.inventory.ContainerMachine;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageRedstoneModeToServer;
import mustapelto.deepmoblearning.common.tiles.TileEntityMachine;
import mustapelto.deepmoblearning.common.util.Point;
import mustapelto.deepmoblearning.common.util.Rect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class GuiMachine extends GuiContainerBase {
    protected final TileEntityMachine tileEntity;
    private final Point redstoneModeButtonLocation;
    private ButtonRedstoneMode redstoneModeButton;

    //
    // INIT
    //

    public GuiMachine(TileEntityMachine tileEntity,
                      EntityPlayer player,
                      World world,
                      ContainerMachine container,
                      int width,
                      int height,
                      Point redstoneModeButtonLocation) {
        super(player, world, container, width, height);
        this.tileEntity = tileEntity;
        this.tileEntity.setGuiOpen(true);
        this.redstoneModeButtonLocation = redstoneModeButtonLocation;
    }

    @Override
    public void onGuiClosed() {
        tileEntity.setGuiOpen(false);
        super.onGuiClosed();
    }

    //
    // BUTTONS
    //

    @Override
    protected void initButtons() {
        redstoneModeButton = new ButtonRedstoneMode(0, guiLeft + redstoneModeButtonLocation.X, guiTop + redstoneModeButtonLocation.Y, tileEntity.getRedstoneMode());
    }

    @Override
    protected void rebuildButtonList() {
        super.rebuildButtonList();
        buttonList.add(redstoneModeButton);
    }

    @Override
    protected void handleButtonPress(ButtonBase button, int mouseButton) {
        if (button instanceof ButtonRedstoneMode) {
            ButtonRedstoneMode redstoneModeButton = (ButtonRedstoneMode) button;
            if (mouseButton == 0)
                redstoneModeButton.setRedstoneMode(redstoneModeButton.getRedstoneMode().next());
            else if (mouseButton == 1)
                redstoneModeButton.setRedstoneMode(redstoneModeButton.getRedstoneMode().prev());

            DMLPacketHandler.sendToServer(new MessageRedstoneModeToServer(tileEntity, redstoneModeButton.getRedstoneMode()));
        }
    }

    protected void drawEnergyBar(Rect energyBar, Point energyBarTextureLocation) {
        int energyBarHeight = (int)(((float) tileEntity.getEnergy() / tileEntity.getMaxEnergy()) * energyBar.HEIGHT);
        int energyBarOffset = energyBar.HEIGHT - energyBarHeight;
        drawTexturedModalRect(
                guiLeft + energyBar.LEFT,
                guiTop + energyBar.TOP + energyBarOffset,
                energyBarTextureLocation.X,
                energyBarTextureLocation.Y,
                energyBar.WIDTH,
                energyBarHeight
        );
    }
}
