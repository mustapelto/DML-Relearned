package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.common.inventory.ContainerSimulationChamber;
import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.metadata.MobMetaData;
import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SimulationChamberGui extends GuiContainer {
    private final FontRenderer fontRenderer;
    private final World world;
    private final TileEntitySimulationChamber simulationChamber; // Sim Chamber that opened this GUI

    // GUI DIMENSIONS
    private static final int WIDTH = 232;
    private static final int HEIGHT = 230;

    private static final int ROW_SPACING = 12;

    private static final Rect DATA_BAR = new Rect(13, 47, 8,87);
    private static final Rect ENERGY_BAR = new Rect(211, 47, 8, 87);

    public SimulationChamberGui(TileEntity tileEntity, EntityPlayer player, World world) {
        super(new ContainerSimulationChamber(tileEntity, player.inventory));

        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        this.world = world;
        simulationChamber = (TileEntitySimulationChamber) tileEntity;

        xSize = WIDTH;
        ySize = HEIGHT;

        simulationChamber.setGuiState(true);
    }

    @Override
    public void onGuiClosed() {
        simulationChamber.setGuiState(false);
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        final int x = mouseX - guiLeft;
        final int y = mouseY - guiTop;

        List<String> tooltip = new ArrayList<>();

        if (DATA_BAR.isInside(x, y)) {
            if (simulationChamber.hasDataModel()) {
                ItemStack dataModel = simulationChamber.getDataModel();
                if (!DataModelHelper.isAtMaxTier(dataModel)) {
                    String currentData = String.valueOf(DataModelHelper.getCurrentTierDataCount(dataModel));
                    String maxData = String.valueOf(DataModelHelper.getTierRequiredData(dataModel));
                    tooltip.add(I18n.format("deepmoblearning.simulation_chamber.tooltip.model_data", currentData + "/" + maxData));
                } else {
                    tooltip.add(I18n.format("deepmoblearning.simulation_chamber.tooltip.model_max"));
                }
            } else {
                tooltip.add(I18n.format("deepmoblearning.simulation_chamber.tooltip.model_missing"));
            }
            drawHoveringText(tooltip, x + 2, y + 2);
        } else if (ENERGY_BAR.isInside(x, y)) {
            String currentEnergy = String.valueOf(simulationChamber.getEnergy());
            String maxEnergy = String.valueOf(simulationChamber.getMaxEnergy());
            tooltip.add(currentEnergy + "/" + maxEnergy + " RF");
            if (simulationChamber.hasDataModel()) {
                int energyDrain = simulationChamber.getSimulationEnergyCost();
                tooltip.add(I18n.format("deepmoblearning.simulation_chamber.tooltip.sim_cost", energyDrain));
            }
            drawHoveringText(tooltip, x - 90, y - 16);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        final int topStart = guiTop - 3;

        final MobMetaData mobMetaData = DataModelHelper.getMobMetaData(simulationChamber.getDataModel());
        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        textureManager.bindTexture(GuiRegistry.SIMULATION_CHAMBER.BASE);
        GlStateManager.color(1f, 1f, 1f, 1f);

        // Main GUI
        drawTexturedModalRect(guiLeft + 8, guiTop, 0, 0, 216, 141);

        // Data Model Slot
        final Rect dmSlot = ContainerSimulationChamber.DATA_MODEL_SLOT;
        drawTexturedModalRect(guiLeft + dmSlot.LEFT - 1, guiTop + dmSlot.TOP - 1, 0, 141, dmSlot.WIDTH, dmSlot.HEIGHT);

        // Current energy
        int energyBarHeight = (int)(((float) simulationChamber.getEnergy() / simulationChamber.getMaxEnergy()) * ENERGY_BAR.HEIGHT);
        int energyBarOffset = ENERGY_BAR.HEIGHT - energyBarHeight;
        drawTexturedModalRect(guiLeft + ENERGY_BAR.LEFT, guiTop + ENERGY_BAR.TOP + energyBarOffset, 25, 141, 7, energyBarHeight);

        // Player inventory
        textureManager.bindTexture(GuiRegistry.DEFAULT_GUI);
        drawTexturedModalRect(guiLeft + 28, guiTop + 145, 0, 0, 176, 90);
    }
}
