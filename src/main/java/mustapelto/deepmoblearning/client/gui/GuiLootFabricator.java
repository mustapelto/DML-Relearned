package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonItemDeselect;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonItemSelect;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonPageSelect;
import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageLootFabOutputItemToServer;
import mustapelto.deepmoblearning.common.tiles.TileEntityLootFabricator;
import mustapelto.deepmoblearning.common.util.MathHelper;
import mustapelto.deepmoblearning.common.util.Point;
import mustapelto.deepmoblearning.common.util.Rect;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class GuiLootFabricator extends GuiMachine {
    // GUI TEXTURES
    public static final ResourceLocation BASE_TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/loot_fabricator.png");

    // GUI DIMENSIONS
    private static final int WIDTH = 177;
    private static final int HEIGHT = 230;
    private static final int MAIN_GUI_WIDTH = 177;
    private static final int MAIN_GUI_HEIGHT = 83;

    // ITEM SLOT LOCATIONS
    public static final Point INPUT_SLOT = new Point(79, 62);
    public static final Point OUTPUT_FIRST_SLOT = new Point(101, 7);
    public static final int OUTPUT_SLOT_SIDE_LENGTH = 18;

    // BUTTON LOCATIONS
    private static final Point REDSTONE_BUTTON = new Point(-20, 0);
    private static final Point OUTPUT_SELECT_LIST = new Point(14, 6);
    private static final int OUTPUT_SELECT_LIST_PADDING = 2;
    private static final int OUTPUT_SELECT_LIST_GUTTER = 1;
    private static final int OUTPUT_SELECT_BUTTON_SIZE = 18;
    private static final Point PREV_PAGE_BUTTON = new Point(13, 66);
    private static final Point NEXT_PAGE_BUTTON = new Point(44, 66);
    private static final Point DESELECT_BUTTON = new Point(79, 4);

    // BUTTON-RELATED VARIABLES
    private static final int ITEMS_PER_PAGE = 9;
    private static final int ITEM_SELECT_BUTTON_INDEX_OFFSET = 100;

    // PROGRESS AND ENERGY BAR
    private static final Rect ENERGY_BAR = new Rect(4, 6, 7, 71);
    private static final Point ENERGY_BAR_TEXTURE_LOCATION = new Point(0, 83);
    private static final Rect PROGRESS_BAR = new Rect(84, 22, 6,36);
    private static final Point PROGRESS_BAR_TEXTURE_LOCATION = new Point(7, 83);
    private static final Point ERROR_BAR_TEXTURE_LOCATION = new Point(13, 83);
    private static final long ERROR_BAR_CYCLE = 20; // Duration of one on-off cycle (ticks)

    // STATE VARIABLES
    private final TileEntityLootFabricator lootFabricator;

    private MobMetadata currentMobMetadata;
    private ImmutableList<ItemStack> lootItemList;
    private int currentOutputItemPage = -1;
    private int totalOutputItemPages = 0;
    private int currentOutputItemIndex = -1; // Index of output item in list of all available outputs

    private final List<ButtonItemSelect> outputSelectButtons = new ArrayList<>();
    private ButtonPageSelect nextPageButton;
    private ButtonPageSelect prevPageButton;
    private ButtonItemDeselect deselectButton;

    private CraftingError craftingError = CraftingError.NONE;
    private long ticks = 0; // Ticks since GUI was opened (used for error bar animation)

    //
    // INIT
    //

    public GuiLootFabricator(TileEntityLootFabricator tileEntity, EntityPlayer player, World world) {
        super(tileEntity, player, world, WIDTH, HEIGHT, REDSTONE_BUTTON);
        lootFabricator = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();

        currentOutputItemIndex = lootFabricator.getOutputItemIndex();
        currentOutputItemPage = currentOutputItemIndex / ITEMS_PER_PAGE;

        resetOutputData(lootFabricator.getMobMetadata(), true);
    }

    //
    // UPDATE
    //

    @Override
    public void updateScreen() {
        super.updateScreen();

        ticks++;

        // Rebuild output selection if Pristine Matter type changed
        MobMetadata lootFabMetadata = lootFabricator.getMobMetadata();
        if (currentMobMetadata != lootFabMetadata)
            resetOutputData(lootFabMetadata, false);

        if (!lootFabricator.isRedstoneActive())
            craftingError = CraftingError.REDSTONE;
        else if (!lootFabricator.hasPristineMatter())
            craftingError = CraftingError.NO_PRISTINE;
        else if (currentOutputItemIndex == -1)
            craftingError = CraftingError.NO_OUTPUT_SELECTED;
        else if (!lootFabricator.hasRoomForOutput())
            craftingError = CraftingError.OUTPUT_FULL;
        else if (!lootFabricator.hasEnergyForCrafting())
            craftingError = CraftingError.NO_ENERGY;
        else
            craftingError = CraftingError.NONE;
    }

    private void resetOutputData(MobMetadata newData, boolean preselectedOutput) {
        currentMobMetadata = newData;

        if (currentMobMetadata == null) {
            setInputEmpty();
            return;
        }

        lootItemList = currentMobMetadata.getLootItemList();

        if (lootItemList.isEmpty()) {
            setInputEmpty();
            return;
        }

        totalOutputItemPages = MathHelper.DivideAndRoundUp(lootItemList.size(), ITEMS_PER_PAGE);
        setPageButtonsEnabled(totalOutputItemPages > 1);

        if (!preselectedOutput) {
            currentOutputItemIndex = -1;
            currentOutputItemPage = 0;
        }

        rebuildOutputSelectButtons();
    }

    private void rebuildOutputSelectButtons() {
        outputSelectButtons.clear();
        constructOutputSelectButtonRows(currentOutputItemPage * ITEMS_PER_PAGE);
        buttonListNeedsRebuild = true;
    }

    private void constructOutputSelectButtonRows(int firstItemIndex) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int indexRelative = row * 3 + col;
                int indexAbsolute = firstItemIndex + indexRelative;
                if (indexAbsolute >= lootItemList.size())
                    return; // End of list reached -> abort
                outputSelectButtons.add(new ButtonItemSelect(
                        ITEM_SELECT_BUTTON_INDEX_OFFSET + indexRelative,
                        guiLeft + OUTPUT_SELECT_LIST.X + OUTPUT_SELECT_LIST_PADDING + col * (OUTPUT_SELECT_BUTTON_SIZE + OUTPUT_SELECT_LIST_GUTTER),
                        guiTop + OUTPUT_SELECT_LIST.Y + OUTPUT_SELECT_LIST_PADDING + row * (OUTPUT_SELECT_BUTTON_SIZE + OUTPUT_SELECT_LIST_GUTTER),
                        lootItemList.get(indexAbsolute),
                        indexAbsolute,
                        indexAbsolute == currentOutputItemIndex
                ));
            }
        }
    }

    /**
     * Pristine Matter input or loot list empty -> set all output-related values to "invalid"
     */
    private void setInputEmpty() {
        setOutputItemIndex(-1);
        currentOutputItemPage = -1;
        totalOutputItemPages = 0;
        outputSelectButtons.clear();
        setPageButtonsEnabled(false);
        buttonListNeedsRebuild = true;
    }

    private void setOutputItem(int index) {
        setOutputItemIndex(index);
        for (int i = 0; i < outputSelectButtons.size(); i++) {
            outputSelectButtons.get(i).setSelected(index != -1 && (i == (index % ITEMS_PER_PAGE)));
        }
        DMLPacketHandler.sendToServer(new MessageLootFabOutputItemToServer(lootFabricator, index));
    }

    private void setOutputItemIndex(int index) {
        currentOutputItemIndex = index;
        deselectButton.setDisplayStack(index == -1 ? ItemStack.EMPTY : lootItemList.get(index));
    }

    //
    // BUTTONS
    //

    @Override
    protected void initButtons() {
        super.initButtons();

        prevPageButton = new ButtonPageSelect(10, guiLeft + PREV_PAGE_BUTTON.X, guiTop + PREV_PAGE_BUTTON.Y, ButtonPageSelect.Direction.PREV);
        nextPageButton = new ButtonPageSelect(11, guiLeft + NEXT_PAGE_BUTTON.X, guiTop + NEXT_PAGE_BUTTON.Y, ButtonPageSelect.Direction.NEXT);
        deselectButton = new ButtonItemDeselect(12, guiLeft + DESELECT_BUTTON.X, guiTop + DESELECT_BUTTON.Y);

        // Output select buttons are initialized through resetOutputData
    }

    @Override
    protected void rebuildButtonList() {
        super.rebuildButtonList();

        buttonList.add(prevPageButton);
        buttonList.add(nextPageButton);
        buttonList.add(deselectButton);

        buttonList.addAll(outputSelectButtons);
    }

    @Override
    protected void handleButtonPress(ButtonBase button, int mouseButton) {
        if (mouseButton == 0 && button instanceof ButtonPageSelect) {
            ButtonPageSelect pageSelectButton = (ButtonPageSelect) button;
            if (pageSelectButton.getDirection() == ButtonPageSelect.Direction.PREV) {
                currentOutputItemPage--;
                if (currentOutputItemPage < 0)
                    currentOutputItemPage = totalOutputItemPages - 1;
            } else {
                currentOutputItemPage++;
                if (currentOutputItemPage >= totalOutputItemPages)
                    currentOutputItemPage = 0;
            }
            rebuildOutputSelectButtons();
        } else if (mouseButton == 0 && button instanceof ButtonItemSelect){
            ButtonItemSelect itemSelectButton = (ButtonItemSelect) button;
            setOutputItem(itemSelectButton.getIndex());
        } else if (mouseButton == 0 && button instanceof ButtonItemDeselect) {
            setOutputItem(-1);
        } else
            super.handleButtonPress(button, mouseButton);
    }

    private void setPageButtonsEnabled(boolean enabled) {
        prevPageButton.enabled = enabled;
        nextPageButton.enabled = enabled;
    }

    //
    // DRAWING
    //

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Draw item stacks on buttons (can't be done in button draw method due to z order problems)
        RenderHelper.enableGUIStandardItemLighting();

        outputSelectButtons.forEach(button -> drawItemStackWithOverlay(button.getStack(), button.x - guiLeft, button.y - guiTop));
        if (currentOutputItemIndex != -1)
            drawItemStackWithOverlay(deselectButton.getDisplayStack(), DESELECT_BUTTON.X, DESELECT_BUTTON.Y);

        RenderHelper.enableStandardItemLighting();

        // Draw button tooltips (after items to ensure correct z order)
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // Draw energy bar tooltip
        int mouseRelativeX = mouseX - guiLeft;
        int mouseRelativeY = mouseY - guiTop;

        if (ENERGY_BAR.isInside(mouseRelativeX, mouseRelativeY)) {
            String currentEnergy = String.valueOf(tileEntity.getEnergy());
            String maxEnergy = String.valueOf(tileEntity.getMaxEnergy());

            List<String> tooltip = new ArrayList<>();
            tooltip.add(currentEnergy + "/" + maxEnergy + " RF");
            tooltip.add(I18n.format("deepmoblearning.loot_fabricator.tooltip.crafting_cost", tileEntity.getCraftingEnergyCost()));
            drawHoveringText(tooltip, mouseRelativeX, mouseRelativeY);
        }

        // Draw progress bar error tooltip
        if (craftingError != CraftingError.NONE && PROGRESS_BAR.isInside(mouseRelativeX, mouseRelativeY)) {
            String tooltip = "";

            switch (craftingError) {
                case NO_ENERGY:
                    tooltip = I18n.format("deepmoblearning.loot_fabricator.error.no_energy");
                    break;
                case REDSTONE:
                    tooltip = I18n.format("deepmoblearning.loot_fabricator.error.redstone");
                    break;
                case NO_PRISTINE:
                    tooltip = I18n.format("deepmoblearning.loot_fabricator.error.no_pristine");
                    break;
                case NO_OUTPUT_SELECTED:
                    tooltip = I18n.format("deepmoblearning.loot_fabricator.error.no_output_selected");
                    break;
                case OUTPUT_FULL:
                    tooltip = I18n.format("deepmoblearning.loot_fabricator.error.output_full");
                    break;
            }

            drawHoveringText(tooltip, mouseRelativeX, mouseRelativeY);
        }
    }

    private void drawItemStackWithOverlay(ItemStack stack, int x, int y) {
        int count = stack.getCount();
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, count > 1 ? String.valueOf(count) : "");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Main GUI
        textureManager.bindTexture(BASE_TEXTURE);
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, MAIN_GUI_WIDTH, MAIN_GUI_HEIGHT);

        // Crafting progress
        int progressBarHeight = (int) (tileEntity.getRelativeCraftingProgress() * PROGRESS_BAR.HEIGHT);
        int progressBarOffset = PROGRESS_BAR.HEIGHT - progressBarHeight;
        drawTexturedModalRect(guiLeft + PROGRESS_BAR.LEFT,
                guiTop + PROGRESS_BAR.TOP + progressBarOffset,
                PROGRESS_BAR_TEXTURE_LOCATION.X,
                PROGRESS_BAR_TEXTURE_LOCATION.Y,
                PROGRESS_BAR.WIDTH,
                progressBarHeight);

        // Crafting error (flashing red bar over progress bar)
        if (craftingError != CraftingError.NONE && (ticks % ERROR_BAR_CYCLE < (ERROR_BAR_CYCLE / 2))) {
            drawTexturedModalRect(guiLeft + PROGRESS_BAR.LEFT,
                    guiTop + PROGRESS_BAR.TOP,
                    ERROR_BAR_TEXTURE_LOCATION.X,
                    ERROR_BAR_TEXTURE_LOCATION.Y,
                    PROGRESS_BAR.WIDTH,
                    PROGRESS_BAR.HEIGHT);
        }

        // Energy bar
        int energyBarHeight = (int)(((float) tileEntity.getEnergy() / tileEntity.getMaxEnergy()) * ENERGY_BAR.HEIGHT);
        int energyBarOffset = ENERGY_BAR.HEIGHT - energyBarHeight;
        drawTexturedModalRect(guiLeft + ENERGY_BAR.LEFT,
                guiTop + ENERGY_BAR.TOP + energyBarOffset,
                ENERGY_BAR_TEXTURE_LOCATION.X,
                ENERGY_BAR_TEXTURE_LOCATION.Y,
                ENERGY_BAR.WIDTH,
                energyBarHeight);

        // Player inventory
        drawPlayerInventory(guiLeft, guiTop + 88);

        outputSelectButtons.forEach(button -> button.drawButton(mc, mouseX, mouseY, partialTicks));
        prevPageButton.drawButton(mc, mouseX, mouseY, partialTicks);
        nextPageButton.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    private enum CraftingError {
        NONE,
        NO_ENERGY,
        REDSTONE,
        NO_PRISTINE,
        NO_OUTPUT_SELECTED,
        OUTPUT_FULL
    }
}
