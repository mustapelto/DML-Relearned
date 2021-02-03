package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.DMLConstants.GuiColors;
import mustapelto.deepmoblearning.common.inventory.ContainerSimulationChamber;
import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.Rect;
import mustapelto.deepmoblearning.common.util.StringAnimator;
import mustapelto.deepmoblearning.common.util.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulationChamberGui extends GuiContainer {
    private final FontRenderer fontRenderer;
    private final TileEntitySimulationChamber simulationChamber; // Sim Chamber that opened this GUI

    // GUI DIMENSIONS
    private static final int WIDTH = 232;
    private static final int HEIGHT = 230;

    private static final int ROW_SPACING = 12;

    // XP and Energy Bar locations
    private static final Rect DATA_BAR = new Rect(13, 47, 8,87);
    private static final Rect ENERGY_BAR = new Rect(211, 47, 8, 87);

    // Item slot locations
    public static final Rect DATA_MODEL_SLOT = new Rect(-14, 0, 18, 18);
    public static final Rect POLYMER_SLOT = new Rect(192, 7, 18, 18);
    public static final Rect LIVING_MATTER_SLOT = new Rect(182, 27, 18, 18);
    public static final Rect PRISTINE_MATTER_SLOT = new Rect(202, 27, 18, 18);

    // Button locations
    public static final Rect REDSTONE_BUTTON = new Rect(-14, 24, 18, 18);

    // Animators for animated strings
    private final StringAnimator progressAnimator = new StringAnimator(); // Used to display simulation progress
    private final StringAnimator emptyDisplayAnimator = new StringAnimator(); // Used to display empty screen ("blinking cursor")
    private final StringAnimator dataModelErrorAnimator = new StringAnimator(); // Used to display error messages relating to data model
    private final StringAnimator simulationErrorAnimator= new StringAnimator(); // Used to display other errors (no polymer/energy, output full)

    private ItemStack dataModel; // Data Model currently inside Simulation Chamber

    private DataModelError dataModelError = DataModelError.NONE; // Error with model (missing/faulty)?
    private SimulationError simulationError = SimulationError.NONE; // Other error (missing polymer/low energy/output full)?
    private boolean redstoneDeactivated = false; // Is simulation chamber deactivated by redstone signal?
    private int currentIteration; // Saves data model's current iteration so we don't update display if iteration hasn't changed
    private boolean currentPristineSuccess; // Saves data model's current pristine success state so we don't update display if iteration hasn't changed

    private int currentTick = 0; // Ticks since GUI was opened
    private float lastPartial = 0; // Time when GUI was last drawn (ticks + partial tick)

    private final RedstoneModeButton redstoneModeButton;

    public SimulationChamberGui(TileEntitySimulationChamber tileEntity, EntityPlayer player) {
        super(new ContainerSimulationChamber(tileEntity, player.inventory));

        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        simulationChamber = tileEntity;

        xSize = WIDTH;
        ySize = HEIGHT;

        simulationChamber.setGuiState(true);

        dataModel = simulationChamber.getDataModel();
        prepareStringAnimators();

        redstoneModeButton = new RedstoneModeButton(this, simulationChamber, REDSTONE_BUTTON.LEFT, REDSTONE_BUTTON.TOP);
    }

    @Override
    public void onGuiClosed() {
        simulationChamber.setGuiState(false);
        super.onGuiClosed();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (redstoneModeButton.isHovered(mouseX - guiLeft, mouseY - guiTop)) {
            redstoneModeButton.click(mouseButton);
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        currentTick++; // Advance tick counter
        dataModel = simulationChamber.getDataModel(); // Update data model

        //
        // Check for Data Model errors and update animator
        //
        if (!simulationChamber.hasDataModel()) {
            if (dataModelError == DataModelError.MISSING)
                return;

            dataModelErrorAnimator.setString(AnimatedString.ERROR_DATA_MODEL_TEXT_1, I18n.format("deepmoblearning.simulation_chamber.error_text.no_data_model_1"));
            dataModelErrorAnimator.setString(AnimatedString.ERROR_DATA_MODEL_TEXT_2, I18n.format("deepmoblearning.simulation_chamber.error_text.no_data_model_2"));
            dataModelErrorAnimator.reset();
            dataModelError = DataModelError.MISSING;
            return;
        }

        if (!simulationChamber.canDataModelSimulate()) {
            if (dataModelError == DataModelError.FAULTY)
                return;

            dataModelErrorAnimator.setString(AnimatedString.ERROR_DATA_MODEL_TEXT_1, I18n.format("deepmoblearning.simulation_chamber.error_text.model_cannot_simulate_1"));
            dataModelErrorAnimator.setString(AnimatedString.ERROR_DATA_MODEL_TEXT_2, I18n.format("deepmoblearning.simulation_chamber.error_text.model_cannot_simulate_2"));
            dataModelErrorAnimator.reset();
            dataModelError = DataModelError.FAULTY;
            return;
        }

        // No Data Model errors found
        dataModelError = DataModelError.NONE;
        emptyDisplayAnimator.reset();

        // Check redstone state
        redstoneDeactivated = !simulationChamber.isRedstoneActive();
        if (redstoneDeactivated)
            return;

        //
        // Check for simulation errors and update animator
        //
        if (!simulationChamber.hasPolymerClay() && !simulationChamber.getSimulationState().isSimulationRunning()) {
            // Polymer error only shown if simulation hasn't started already (i.e. can remove remaining polymer while simulation is running)
            if (simulationError == SimulationError.POLYMER)
                return;

            simulationErrorAnimator.setString(AnimatedString.ERROR_SIMULATION_TEXT, I18n.format("deepmoblearning.simulation_chamber.error_text.no_polymer"));
            simulationErrorAnimator.reset();
            simulationError = SimulationError.POLYMER;
            return;
        }

        if (!simulationChamber.hasEnergyForSimulation()) {
            if (simulationError == SimulationError.ENERGY)
                return;

            simulationErrorAnimator.setString(AnimatedString.ERROR_SIMULATION_TEXT, I18n.format("deepmoblearning.simulation_chamber.error_text.no_energy"));
            simulationErrorAnimator.reset();
            simulationError = SimulationError.ENERGY;
            return;
        }

        if ((simulationChamber.isPristineMatterOutputFull() || simulationChamber.isLivingMatterOutputFull())) {
            if (simulationError == SimulationError.OUTPUT)
                return;

            simulationErrorAnimator.setString(AnimatedString.ERROR_SIMULATION_TEXT, I18n.format("deepmoblearning.simulation_chamber.error_text.output_full"));
            simulationErrorAnimator.reset();
            simulationError = SimulationError.OUTPUT;
            return;
        }

        // No simulation errors found
        simulationError = SimulationError.NONE;

        // Update data for current iteration
        int iteration = DataModelHelper.getTotalSimulationCount(dataModel) + 1;
        boolean pristineSuccess = simulationChamber.getSimulationState().isPristineSuccess();

        if ((iteration == currentIteration) && (pristineSuccess == currentPristineSuccess))
            return; // Already updated, no need to do it again

        currentIteration = iteration;
        currentPristineSuccess = pristineSuccess;

        String iterationString = I18n.format("deepmoblearning.simulation_chamber.simulation_text.iteration", iteration);
        progressAnimator.setString(AnimatedString.SIMULATION_ITERATION, iterationString);


        String pristineString = I18n.format("deepmoblearning.simulation_chamber.simulation_text.pristine");
        String successString = TextFormatting.GREEN + I18n.format("deepmoblearning.simulation_chamber.simulation_text.pristine_success") + TextFormatting.RESET;
        String failureString = TextFormatting.RED + I18n.format("deepmoblearning.simulation_chamber.simulation_text.pristine_failure") + TextFormatting.RESET;
        pristineString += " " + (pristineSuccess ? successString : failureString);
        progressAnimator.setString(AnimatedString.SIMULATION_PRISTINE, pristineString);
        progressAnimator.reset();
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
            // Draw Data Bar Tooltip
            if (simulationChamber.hasDataModel()) {
                if (!DataModelHelper.isAtMaxTier(dataModel)) {
                    String currentData = String.valueOf(DataModelHelper.getCurrentTierDataCount(dataModel));
                    String maxData = String.valueOf(DataModelHelper.getTierRequiredData(dataModel));
                    tooltip.add(I18n.format("deepmoblearning.simulation_chamber.tooltip.model_data", currentData + "/" + maxData));
                } else {
                    tooltip.add(I18n.format("deepmoblearning.simulation_chamber.tooltip.model_max"));
                }
                if (!DataModelHelper.canSimulate(dataModel)) {
                    tooltip.add(TextFormatting.RED + I18n.format("deepmoblearning.simulation_chamber.tooltip.model_cannot_simulate") + TextFormatting.RESET);
                }
            } else {
                tooltip.add(I18n.format("deepmoblearning.simulation_chamber.tooltip.model_missing"));
            }
            drawHoveringText(tooltip, x + 2, y + 2);
        } else if (ENERGY_BAR.isInside(x, y)) {
            // Draw Energy Bar Tooltip
            String currentEnergy = String.valueOf(simulationChamber.getEnergy());
            String maxEnergy = String.valueOf(simulationChamber.getMaxEnergy());
            tooltip.add(currentEnergy + "/" + maxEnergy + " RF");
            if (simulationChamber.hasDataModel()) {
                int energyDrain = simulationChamber.getSimulationEnergyCost();
                tooltip.add(I18n.format("deepmoblearning.simulation_chamber.tooltip.sim_cost", energyDrain));
            }
            drawHoveringText(tooltip, x - 90, y - 16);
        } else if (redstoneModeButton.isHovered(x, y)) {
            // Draw Redstone Mode Tooltip
            tooltip = redstoneModeButton.getTooltip();
            drawHoveringText(tooltip, x - 50, y + 2);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        textureManager.bindTexture(GuiRegistry.SIMULATION_CHAMBER.BASE);
        GlStateManager.color(1f, 1f, 1f, 1f);

        // Main GUI
        drawTexturedModalRect(guiLeft + 8, guiTop, 0, 0, 216, 141);

        // Data Model Slot
        drawTexturedModalRect(guiLeft + DATA_MODEL_SLOT.LEFT, guiTop + DATA_MODEL_SLOT.TOP, 0, 141, DATA_MODEL_SLOT.WIDTH, DATA_MODEL_SLOT.HEIGHT);

        // Data Model Experience Bar
        if (dataModelError == DataModelError.NONE) {
            int dataBarHeight;
            if (DataModelHelper.isAtMaxTier(dataModel)) {
                dataBarHeight = DATA_BAR.HEIGHT;
            } else {
                int currentData = DataModelHelper.getCurrentTierDataCount(dataModel);
                int tierMaxData = DataModelHelper.getTierRequiredData(dataModel);
                dataBarHeight = (int) (((float) currentData / tierMaxData) * DATA_BAR.HEIGHT);
            }
            int dataBarOffset = DATA_BAR.HEIGHT - dataBarHeight;
            drawTexturedModalRect(guiLeft + 14, guiTop + DATA_BAR.TOP + dataBarOffset, 18, 141, 7, dataBarHeight);
        }

        // Energy Bar
        int energyBarHeight = (int)(((float) simulationChamber.getEnergy() / simulationChamber.getMaxEnergy()) * ENERGY_BAR.HEIGHT);
        int energyBarOffset = ENERGY_BAR.HEIGHT - energyBarHeight;
        drawTexturedModalRect(guiLeft + ENERGY_BAR.LEFT, guiTop + ENERGY_BAR.TOP + energyBarOffset, 25, 141, 7, energyBarHeight);

        // Player inventory
        textureManager.bindTexture(GuiRegistry.DEFAULT_GUI);
        drawTexturedModalRect(guiLeft + 28, guiTop + 145, 0, 0, 176, 90);

        // Redstone Mode button
        redstoneModeButton.drawButton(mouseX - guiLeft, mouseY - guiTop);

        // Calculate delta time since last redraw (used to advance string animations)
        float currentPartial = currentTick + partialTicks;
        float advanceAmount = currentPartial - lastPartial;
        lastPartial = currentPartial;

        drawInfoboxText(advanceAmount, guiLeft + 18, guiTop + 9);
        drawConsoleText(advanceAmount, guiLeft + 29, guiTop + 51);
    }

    private void drawInfoboxText(float advanceAmount, int left, int top) {
        List<String> strings = new ArrayList<>();

        if (dataModelError == DataModelError.NONE) {
            String tier = I18n.format("deepmoblearning.simulation_chamber.data_model_info.tier");
            String iterations = I18n.format("deepmoblearning.simulation_chamber.data_model_info.iterations");
            String pristine = I18n.format("deepmoblearning.simulation_chamber.data_model_info.pristine");
            strings.add(tier + ": " + DataModelHelper.getTierDisplayNameFormatted(dataModel));
            strings.add(iterations + ": " + DataModelHelper.getTotalSimulationCount(dataModel));
            strings.add(pristine + ": " + DataModelHelper.getPristineChance(dataModel));
        } else {
            dataModelErrorAnimator.advance(advanceAmount);
            strings = dataModelErrorAnimator.getCurrentStrings();
        }

        drawStrings(strings, left, top);
    }

    private void drawConsoleText(float advanceAmount, int left, int top) {
        List<String> strings;

        if (dataModelError != DataModelError.NONE) {
            emptyDisplayAnimator.advance(advanceAmount);
            strings = emptyDisplayAnimator.getCurrentStrings();
        } else if (redstoneDeactivated) {
            strings = new ArrayList<>();
            strings.add(TextFormatting.RED + TextHelper.getDashedLine(28) + TextFormatting.RESET);
            strings.add(TextFormatting.RED + TextHelper.pad(I18n.format("deepmoblearning.simulation_chamber.redstone_deactivated_1"), 28) + TextFormatting.RESET);
            strings.add(TextFormatting.RED + TextHelper.pad(I18n.format("deepmoblearning.simulation_chamber.redstone_deactivated_2"), 28) + TextFormatting.RESET);
            strings.add(TextFormatting.RED + TextHelper.getDashedLine(28) + TextFormatting.RESET);
        } else if (simulationError != SimulationError.NONE) {
            simulationErrorAnimator.advance(advanceAmount);
            strings = simulationErrorAnimator.getCurrentStrings();
        } else {
            float relativeProgress = simulationChamber.getSimulationState().getSimulationRelativeProgress();
            progressAnimator.goToRelativePosition(relativeProgress);
            strings = progressAnimator.getCurrentStrings();
        }

        drawStrings(strings, left, top);
    }

    private void drawStrings(List<String> strings, int left, int top) {
        for (int i = 0; i < strings.size(); i++) {
            drawString(fontRenderer, strings.get(i), left, top + i * ROW_SPACING, GuiColors.WHITE);
        }
    }

    private void prepareStringAnimators() {
        String blinkingCursor = " _"; // Space so this looks like it's blinking
        float cursorSpeed = 16;
        String simulationLaunching = I18n.format("deepmoblearning.simulation_chamber.simulation_text.launching");
        String simulationLoading = I18n.format("deepmoblearning.simulation_chamber.simulation_text.loading");
        String simulationAssessing = I18n.format("deepmoblearning.simulation_chamber.simulation_text.assessing");
        String simulationEngaged = I18n.format("deepmoblearning.simulation_chamber.simulation_text.engaged");
        String simulationProcessing = I18n.format("deepmoblearning.simulation_chamber.simulation_text.processing") + " . . . . ."; // Padding so this line is displayed a little longer
        String error = I18n.format("deepmoblearning.simulation_chamber.error_text.error");

        progressAnimator.addString(AnimatedString.SIMULATION_LAUNCHING, simulationLaunching);
        progressAnimator.addString(AnimatedString.SIMULATION_ITERATION, ""); // gets set in update method
        progressAnimator.addString(AnimatedString.SIMULATION_LOADING, simulationLoading);
        progressAnimator.addString(AnimatedString.SIMULATION_ASSESSING, simulationAssessing);
        progressAnimator.addString(AnimatedString.SIMULATION_ENGAGED, simulationEngaged);
        progressAnimator.addString(AnimatedString.SIMULATION_PRISTINE, ""); // gets set in update method
        progressAnimator.addString(AnimatedString.SIMULATION_PROCESSING, simulationProcessing);

        emptyDisplayAnimator.addString(AnimatedString.UNDERLINE, blinkingCursor, cursorSpeed, true);

        dataModelErrorAnimator.addString(AnimatedString.ERROR_DATA_MODEL_HEADING, error);
        dataModelErrorAnimator.addString(AnimatedString.ERROR_DATA_MODEL_TEXT_1, ""); // gets set in update method
        dataModelErrorAnimator.addString(AnimatedString.ERROR_DATA_MODEL_TEXT_2, ""); // gets set in update method

        simulationErrorAnimator.addString(AnimatedString.ERROR_SIMULATION_HEADING, error);
        simulationErrorAnimator.addString(AnimatedString.ERROR_SIMULATION_TEXT, ""); // gets set in update method
        simulationErrorAnimator.addString(AnimatedString.UNDERLINE, blinkingCursor, cursorSpeed, true);
    }

    public enum AnimatedString {
        UNDERLINE,
        SIMULATION_LAUNCHING,
        SIMULATION_ITERATION,
        SIMULATION_LOADING,
        SIMULATION_ASSESSING,
        SIMULATION_ENGAGED,
        SIMULATION_PRISTINE,
        SIMULATION_PROCESSING,
        ERROR_DATA_MODEL_HEADING,
        ERROR_DATA_MODEL_TEXT_1,
        ERROR_DATA_MODEL_TEXT_2,
        ERROR_SIMULATION_HEADING,
        ERROR_SIMULATION_TEXT
    }

    private enum DataModelError {
        NONE,
        MISSING,
        FAULTY
    }

    private enum SimulationError {
        NONE,
        ENERGY,
        POLYMER,
        OUTPUT
    }
}
