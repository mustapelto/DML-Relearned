package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonRedstoneMode;
import mustapelto.deepmoblearning.common.tiles.TileEntityLootFabricator;
import mustapelto.deepmoblearning.common.util.Point;
import mustapelto.deepmoblearning.common.util.Rect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiLootFabricator extends GuiMachine {
    // GUI TEXTURES
    public static final ResourceLocation BASE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/default_gui.png");

    // GUI DIMENSIONS
    private static final int WIDTH = 176;
    private static final int HEIGHT = 230;

    // ITEM SLOT LOCATIONS
    public static final Point INPUT_SLOT = new Point(79, 62);
    public static final Point OUTPUT_FIRST_SLOT = new Point(100, 7);
    public static final int OUTPUT_SLOT_SIDE_LENGTH = 18;

    // BUTTON LOCATIONS
    private static final Rect REDSTONE_BUTTON = new Rect(-20, 0, 18, 18);

    private final TileEntityLootFabricator lootFabricator;

    public GuiLootFabricator(TileEntityLootFabricator tileEntity, EntityPlayer player, World world) {
        super(tileEntity, player, world, WIDTH, HEIGHT);
        lootFabricator = tileEntity;

        buttonList.add(new ButtonRedstoneMode(0, REDSTONE_BUTTON.LEFT, REDSTONE_BUTTON.TOP, tileEntity));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
