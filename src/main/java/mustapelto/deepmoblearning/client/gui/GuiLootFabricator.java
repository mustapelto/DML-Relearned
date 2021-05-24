package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonItemDeselect;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonItemSelect;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonPageSelect;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.tiles.TileEntityLootFabricator;
import mustapelto.deepmoblearning.common.util.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static mustapelto.deepmoblearning.DMLConstants.Gui.LootFabricator.*;

public class GuiLootFabricator extends GuiMachine {
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

    private void drawItemStackWithOverlay(ItemStack stack, int x, int y) {
        int count = stack.getCount();
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(
                fontRenderer,
                stack,
                x,
                y,
                count > 1 ? String.valueOf(count) : ""
        );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Main GUI
        textureManager.bindTexture(TEXTURE);
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawTexturedModalRect(
                guiLeft + MAIN_GUI.LEFT,
                guiTop + MAIN_GUI.TOP,
                MAIN_GUI_TEXTURE_LOCATION.X,
                MAIN_GUI_TEXTURE_LOCATION.Y,
                MAIN_GUI.WIDTH,
                MAIN_GUI.HEIGHT
        );

        // Crafting progress
        int progressBarHeight = (int) (tileEntity.getRelativeCraftingProgress() * PROGRESS_BAR.HEIGHT);
        int progressBarOffset = PROGRESS_BAR.HEIGHT - progressBarHeight;
        drawTexturedModalRect(
                guiLeft + PROGRESS_BAR.LEFT,
                guiTop + PROGRESS_BAR.TOP + progressBarOffset,
                PROGRESS_BAR_TEXTURE_LOCATION.X,
                PROGRESS_BAR_TEXTURE_LOCATION.Y,
                PROGRESS_BAR.WIDTH,
                progressBarHeight
        );

        // Crafting error (flashing red bar over progress bar)
        if (craftingError != CraftingError.NONE && (ticks % ERROR_BAR_CYCLE < (ERROR_BAR_CYCLE / 2))) {
            drawTexturedModalRect(
                    guiLeft + PROGRESS_BAR.LEFT,
                    guiTop + PROGRESS_BAR.TOP + 1,
                    ERROR_BAR_TEXTURE_LOCATION.X,
                    ERROR_BAR_TEXTURE_LOCATION.Y,
                    PROGRESS_BAR.WIDTH,
                    PROGRESS_BAR.HEIGHT
            );
        }

        drawEnergyBar(ENERGY_BAR, ENERGY_BAR_TEXTURE_LOCATION);

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
