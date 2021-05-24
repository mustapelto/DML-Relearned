package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonItemDeselect;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonItemSelect;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonPageSelect;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static mustapelto.deepmoblearning.DMLConstants.Gui.LootFabricator.*;

public class GuiLootFabricator extends GuiMachine {
    // TEXTURE
    private static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/loot_fabricator.png");
    private static final class TextureCoords {
        private static final Point MAIN_GUI = new Point(0, 0);
        private static final Point ENERGY_BAR = new Point(0, 83);
        private static final Point PROGRESS_BAR = new Point(7, 83);
        private static final Point ERROR_BAR = new Point(13, 83);
    }

    // DIMENSIONS
    private static final int WIDTH = 177;
    private static final int HEIGHT = 230;
    private static final Rect MAIN_GUI = new Rect(0, 0, 177, 83);

    // BUTTONS
    private static final Point REDSTONE_BUTTON = new Point(-20, 0);
    private static final Point OUTPUT_SELECT_LIST = new Point(14, 6);
    private static final int OUTPUT_SELECT_LIST_PADDING = 2;
    private static final int OUTPUT_SELECT_LIST_GUTTER = 1;
    private static final int OUTPUT_SELECT_BUTTON_SIZE = 18;
    private static final Point PREV_PAGE_BUTTON = new Point(13, 66);
    private static final Point NEXT_PAGE_BUTTON = new Point(44, 66);
    private static final Point DESELECT_BUTTON = new Point(79, 4);

    private static final int ITEMS_PER_PAGE = 9;

    private static final int PREV_PAGE_BUTTON_ID = 10;
    private static final int NEXT_PAGE_BUTTON_ID = 11;
    private static final int DESELECT_BUTTON_ID = 12;

    private static final int ITEM_SELECT_BUTTON_ID_OFFSET = 20;

    // PROGRESS AND ENERGY BAR
    private static final Rect ENERGY_BAR = new Rect(4, 6, 7, 71);
    private static final Rect PROGRESS_BAR = new Rect(84, 22, 6,36);
    private static final long ERROR_BAR_CYCLE = 20; // Duration of one on-off cycle (ticks)

    // STATE VARIABLES
    private final TileEntityLootFabricator lootFabricator;

    @Nullable
    private MetadataDataModel pristineMatterMetadata;
    private ImmutableList<ItemStack> lootItemList;
    private int currentOutputItemPage = -1;
    private int totalOutputItemPages = 0;
    private ItemStack outputItem = ItemStack.EMPTY;

    private final List<ButtonItemSelect> outputSelectButtons = new ArrayList<>();
    private ButtonPageSelect nextPageButton;
    private ButtonPageSelect prevPageButton;
    private ButtonItemDeselect deselectButton;

    private CraftingError craftingError = CraftingError.NONE;

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
        pristineMatterMetadata = lootFabricator.getPristineMatterMetadata();
        resetOutputData();
    }

    //
    // UPDATE
    //

    @Override
    public void updateScreen() {
        super.updateScreen();

        ticks++;

        // Rebuild output selection if Pristine Matter type changed
        MetadataDataModel lootFabMetadata = lootFabricator.getPristineMatterMetadata();
        if (pristineMatterMetadata != lootFabMetadata) {
            pristineMatterMetadata = lootFabMetadata;
            resetOutputData();
        }

        if (!lootFabricator.isRedstoneActive())
            craftingError = CraftingError.REDSTONE;
        else if (!lootFabricator.hasPristineMatter())
            craftingError = CraftingError.NO_PRISTINE;
        else if (outputItem == ItemStack.EMPTY)
            craftingError = CraftingError.NO_OUTPUT_SELECTED;
        else if (!lootFabricator.hasRoomForOutput())
            craftingError = CraftingError.OUTPUT_FULL;
        else if (!lootFabricator.hasEnergyForCrafting())
            craftingError = CraftingError.NO_ENERGY;
        else
            craftingError = CraftingError.NONE;
    }

    private void resetOutputData() {
        outputItem = lootFabricator.getOutputItem();
        deselectButton.setDisplayStack(outputItem);

        if (pristineMatterMetadata == null) {
            lootItemList = ImmutableList.of();
            currentOutputItemPage = -1;
            totalOutputItemPages = 0;
            setPageButtonsEnabled(false);
        } else {
            lootItemList = pristineMatterMetadata.getLootItems();
            if (!outputItem.isEmpty()) {
                int currentOutputItemIndex = pristineMatterMetadata.getLootItemIndex(outputItem);
                currentOutputItemPage = currentOutputItemIndex / ITEMS_PER_PAGE;
            } else {
                currentOutputItemPage = 0;
            }
            totalOutputItemPages = MathHelper.divideAndRoundUp(lootItemList.size(), ITEMS_PER_PAGE);
            setPageButtonsEnabled(totalOutputItemPages > 1);
        }

        rebuildOutputSelectButtons();
    }

    private void rebuildOutputSelectButtons() {
        outputSelectButtons.clear();
        constructOutputSelectButtonRows();
        buttonListNeedsRebuild = true;
    }

    private void constructOutputSelectButtonRows() {
        if (currentOutputItemPage < 0)
            return; // Invalid page -> abort

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int indexRelative = row * 3 + col;
                int indexAbsolute = (currentOutputItemPage * ITEMS_PER_PAGE) + indexRelative;
                if (indexAbsolute >= lootItemList.size())
                    return; // End of list reached -> abort
                ItemStack stack = lootItemList.get(indexAbsolute);
                outputSelectButtons.add(new ButtonItemSelect(
                        ITEM_SELECT_BUTTON_ID_OFFSET + indexRelative,
                        guiLeft + OUTPUT_SELECT_LIST.X + OUTPUT_SELECT_LIST_PADDING + col * (OUTPUT_SELECT_BUTTON_SIZE + OUTPUT_SELECT_LIST_GUTTER),
                        guiTop + OUTPUT_SELECT_LIST.Y + OUTPUT_SELECT_LIST_PADDING + row * (OUTPUT_SELECT_BUTTON_SIZE + OUTPUT_SELECT_LIST_GUTTER),
                        stack,
                        indexAbsolute,
                        ItemStack.areItemsEqual(stack, outputItem)
                ));
            }
        }
    }

    private void setOutputItem(int index) {
        for (int i = 0; i < outputSelectButtons.size(); i++) {
            outputSelectButtons.get(i).setSelected(index != -1 && (i == (index % ITEMS_PER_PAGE)));
        }

        if (index < 0 || index >= lootItemList.size())
            outputItem = ItemStack.EMPTY;
        else
            outputItem = lootItemList.get(index);

        deselectButton.setDisplayStack(outputItem);
        lootFabricator.setOutputItem(outputItem);
    }

    //
    // BUTTONS
    //

    @Override
    protected void initButtons() {
        super.initButtons();

        prevPageButton = new ButtonPageSelect(PREV_PAGE_BUTTON_ID, guiLeft + PREV_PAGE_BUTTON.X, guiTop + PREV_PAGE_BUTTON.Y, ButtonPageSelect.Direction.PREV);
        nextPageButton = new ButtonPageSelect(NEXT_PAGE_BUTTON_ID, guiLeft + NEXT_PAGE_BUTTON.X, guiTop + NEXT_PAGE_BUTTON.Y, ButtonPageSelect.Direction.NEXT);
        deselectButton = new ButtonItemDeselect(DESELECT_BUTTON_ID, guiLeft + DESELECT_BUTTON.X, guiTop + DESELECT_BUTTON.Y);

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
        ItemStack deselectStack = deselectButton.getDisplayStack();
        if (!deselectStack.isEmpty())
            drawItemStackWithOverlay(deselectStack, DESELECT_BUTTON.X, DESELECT_BUTTON.Y);

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
        if (craftingError != CraftingError.NONE &&
                PROGRESS_BAR.isInside(mouseRelativeX, mouseRelativeY)) {
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

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Main GUI
        textureManager.bindTexture(TEXTURE);
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawTexturedModalRect(
                guiLeft + MAIN_GUI.LEFT,
                guiTop + MAIN_GUI.TOP,
                TextureCoords.MAIN_GUI.X,
                TextureCoords.MAIN_GUI.Y,
                MAIN_GUI.WIDTH,
                MAIN_GUI.HEIGHT
        );

        // Crafting progress
        int progressBarHeight = (int) (tileEntity.getRelativeCraftingProgress() * PROGRESS_BAR.HEIGHT);
        int progressBarOffset = PROGRESS_BAR.HEIGHT - progressBarHeight;
        drawTexturedModalRect(
                guiLeft + PROGRESS_BAR.LEFT,
                guiTop + PROGRESS_BAR.TOP + progressBarOffset,
                TextureCoords.PROGRESS_BAR.X,
                TextureCoords.PROGRESS_BAR.Y,
                PROGRESS_BAR.WIDTH,
                progressBarHeight
        );

        // Crafting error (flashing red bar over progress bar)
        if (craftingError != CraftingError.NONE && (currentTick % ERROR_BAR_CYCLE < (ERROR_BAR_CYCLE / 2))) {
            drawTexturedModalRect(
                    guiLeft + PROGRESS_BAR.LEFT,
                    guiTop + PROGRESS_BAR.TOP + 1,
                    TextureCoords.ERROR_BAR.X,
                    TextureCoords.ERROR_BAR.Y,
                    PROGRESS_BAR.WIDTH,
                    PROGRESS_BAR.HEIGHT
            );
        }

        drawEnergyBar(ENERGY_BAR, TextureCoords.ENERGY_BAR);

        drawPlayerInventory(guiLeft + PLAYER_INVENTORY.X, guiTop + PLAYER_INVENTORY.Y);
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
