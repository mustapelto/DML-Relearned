package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.DMLConstants.GuiColors;
import mustapelto.deepmoblearning.common.inventory.ContainerDeepLearner;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.metadata.MobMetaData;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.io.IOException;

public class DeepLearnerGui extends GuiContainer {
    private final FontRenderer fontRenderer;
    private final World world;
    private final ItemStack deepLearner; // Deep Learner that opened this GUI
    private NonNullList<ItemStack> dataModels; // Contained Data Models
    private int currentModelIndex = 0; // Currently selected Model for display

    private static final Rect BUTTON_PREV = new Rect(-27, 105, 24, 24);
    private static final Rect BUTTON_NEXT = new Rect(-1, 105, 24, 24);

    private static final int ROW_SPACING = 12;

    public DeepLearnerGui(InventoryPlayer inventoryPlayer, World world, EntityEquipmentSlot equipmentSlot, ItemStack heldItem) {
        super(new ContainerDeepLearner(inventoryPlayer, world, equipmentSlot, heldItem));
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        this.world = world;
        this.deepLearner = heldItem;
        xSize = 338;
        ySize = 235;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    private int nextItemIndex() {
        return currentModelIndex == dataModels.size() - 1 ? 0 : currentModelIndex + 1;
    }

    private int prevItemIndex() {
        return currentModelIndex == 0 ? dataModels.size() - 1 : currentModelIndex - 1;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        if (dataModels.size() > 1) {
            if (isHoveringPrevButton(x, y)) {
                currentModelIndex = prevItemIndex();
            } else if (isHoveringNextButton(x, y)) {
                currentModelIndex = nextItemIndex();
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean isHoveringPrevButton(int x, int y) {
        return x >= BUTTON_PREV.LEFT && x <= BUTTON_PREV.RIGHT && y >= BUTTON_PREV.TOP && y <= BUTTON_PREV.BOTTOM;
    }

    private boolean isHoveringNextButton(int x, int y) {
        return x >= BUTTON_NEXT.LEFT && x <= BUTTON_NEXT.RIGHT && y >= BUTTON_NEXT.TOP && y <= BUTTON_NEXT.BOTTOM;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int left = getGuiLeft();
        int top = getGuiTop();

        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        // Draw main GUI
        textureManager.bindTexture(GuiRegistry.DEEP_LEARNER.BASE);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawTexturedModalRect(left + 41, top, 0, 0, 256, 140);

        // Draw player inventory
        textureManager.bindTexture(GuiRegistry.DEFAULT_GUI);
        drawTexturedModalRect(left + 81, top + 145, 0, 0, 176, 90);

        // Get list of contained Data Models
        NonNullList<ItemStack> deepLearnerStackList = ItemDeepLearner.getContainedItems(deepLearner);
        dataModels = DataModelHelper.getDataModelStacksFromList(deepLearnerStackList);

        // Render model cycle buttons if >1 model inserted
        if (dataModels.size() > 1) {
            renderCycleButtons(textureManager, left, top, mouseX, mouseY);
        }

        // Invalid Model Index -> reset to 0
        if (currentModelIndex >= dataModels.size()) {
            currentModelIndex = 0;
        }

        // At least 1 data model -> display metadata
        if (dataModels.size() > 0) {
            ItemStack currentModelStack = dataModels.get(currentModelIndex);
            MobMetaData mobMetaData = DataModelHelper.getMobMetaData(currentModelStack);

            if (mobMetaData != null) {
                renderMetaData(textureManager, mobMetaData, left, top, currentModelStack);
                renderMobDisplayBox(textureManager, left, top);

                Entity mainEntity = mobMetaData.getEntity(world);

                if (mainEntity != null) {
                    renderEntity(mainEntity,
                            mobMetaData.getDisplayEntityScale(),
                            left + mobMetaData.getDisplayEntityOffsetX(),
                            top + 80 + mobMetaData.getDisplayEntityOffsetY(),
                            partialTicks);
                }

                Entity extraEntity = mobMetaData.getExtraEntity(world);

                if (extraEntity != null) {
                    renderEntity(extraEntity,
                            mobMetaData.getDisplayEntityScale(),
                            left + mobMetaData.getDisplayExtraEntityOffsetX(),
                            top + 80 + mobMetaData.getDisplayExtraEntityOffsetY(),
                            partialTicks);
                }
            }
        } else {
            renderDefaultScreen(left, top);
        }
    }

    private void renderCycleButtons(TextureManager textureManager, int left, int top, int mouseX, int mouseY) {
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        // Draw buttons
        textureManager.bindTexture(GuiRegistry.DEEP_LEARNER.EXTRAS);
        drawTexturedModalRect(left + BUTTON_PREV.LEFT, top + BUTTON_PREV.TOP, 75, 0, BUTTON_PREV.WIDTH, BUTTON_PREV.HEIGHT);
        drawTexturedModalRect(left + BUTTON_NEXT.LEFT, top + BUTTON_NEXT.TOP, 99, 0, BUTTON_NEXT.WIDTH, BUTTON_NEXT.HEIGHT);

        // Hover states
        if (isHoveringPrevButton(x, y)) {
            drawTexturedModalRect(left + BUTTON_PREV.LEFT, top + BUTTON_PREV.TOP, 75, 24, BUTTON_PREV.WIDTH, BUTTON_PREV.HEIGHT);
        } else if (isHoveringNextButton(x, y)) {
            drawTexturedModalRect(left + BUTTON_NEXT.LEFT, top + BUTTON_NEXT.TOP, 99, 24, BUTTON_NEXT.WIDTH, BUTTON_NEXT.HEIGHT);
        }
    }

    private void renderDefaultScreen(int left, int top) {
        int leftStart = left + 49;
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_1"), leftStart, top + ROW_SPACING, GuiColors.AQUA);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_2"), leftStart, top + 2 * ROW_SPACING, GuiColors.WHITE);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_3"), leftStart, top + 3 * ROW_SPACING, GuiColors.WHITE);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_4"), leftStart, top + 4 * ROW_SPACING, GuiColors.WHITE);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_5"), leftStart, top + 6 * ROW_SPACING, GuiColors.WHITE);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_6"), leftStart, top + 7 * ROW_SPACING, GuiColors.WHITE);
    }

    private void renderMetaData(TextureManager textureManager, MobMetaData mobMetaData, int left, int top, ItemStack stack) {
        int leftStart = left + 49;
        int topStart = top - 4;

        String dataModelTier = DataModelHelper.getTierDisplayNameFormatted(stack);
        String nextTier = DataModelHelper.getNextTierDisplayNameFormatted(stack);
        String mobName = mobMetaData.getDisplayName();
        String mobPluralName = mobMetaData.getDisplayNamePlural();
        String[] mobTrivia = mobMetaData.getMobTrivia();
        int totalKills = DataModelHelper.getTotalKillCount(stack);
        int killsToNextTier = DataModelHelper.getKillsToNextTier(stack);
        int numHearts = mobMetaData.getNumberOfHearts();

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.heading_name"),
                leftStart,
                topStart + ROW_SPACING,
                GuiColors.AQUA);

        drawString(fontRenderer,
                mobName,
                leftStart,
                topStart + (2 * ROW_SPACING),
                GuiColors.WHITE);

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.heading_information"),
                leftStart,
                topStart + (3 * ROW_SPACING),
                GuiColors.AQUA);

        for (int i = 0; i < mobTrivia.length; i++) {
            drawString(fontRenderer,
                    mobTrivia[i],
                    leftStart,
                    topStart + (3 * ROW_SPACING) + ((i + 1) * 12),
                    GuiColors.WHITE);
        }

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.model_tier", dataModelTier),
                leftStart,
                topStart + (8 * ROW_SPACING),
                GuiColors.WHITE);

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.defeated", mobPluralName, totalKills),
                leftStart,
                topStart + (9 * ROW_SPACING),
                GuiColors.WHITE);

        drawString(fontRenderer,
                DataModelHelper.isAtMaxTier(stack) ?
                        I18n.format("deepmoblearning.deep_learner.maximum") :
                        I18n.format("deepmoblearning.deep_learner.required", killsToNextTier, nextTier),
                leftStart,
                topStart + (10 * ROW_SPACING),
                GuiColors.WHITE);

        // Draw heart
        textureManager.bindTexture(GuiRegistry.DEEP_LEARNER.BASE);
        drawTexturedModalRect(left + 228, topStart + (2 * ROW_SPACING) - 2, 0, 140, 9, 9);

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.health_points"),
                left + 228,
                topStart + ROW_SPACING,
                GuiColors.AQUA);

        // Obfuscate if hearts == 0 (for models with multiple mobs, e.g. Twilight Forest)
        drawString(fontRenderer,
                numHearts == 0 ?
                        TextFormatting.OBFUSCATED + "10" + TextFormatting.RESET :
                        Integer.toString(numHearts),
                left + 239,
                topStart + (2 * ROW_SPACING) - 1,
                GuiColors.WHITE
                );
    }

    private void renderMobDisplayBox(TextureManager textureManager, int left, int top) {
        textureManager.bindTexture(GuiRegistry.DEEP_LEARNER.EXTRAS);
        drawTexturedModalRect(left - 41, top, 0, 0, 75, 101);
    }

    private void renderEntity(Entity entity, int scale, int x, int y, float partialTicks) {
        EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
        entityRenderer.disableLightmap();

        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);

        // Mob "bobbing" offset
        double heightOffset = Math.sin((world.getTotalWorldTime() + partialTicks) / 16.0) / 8.0;

        // Mob rotating angle
        float angle = (world.getTotalWorldTime() + partialTicks) * 3;

        // High z axis value to prevent clipping
        GlStateManager.translate(0.2f, heightOffset, 15.0f);
        GlStateManager.rotate(angle, 0.0f, 1.0f, 0.0f);

        // Render entity
        Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0f, 0.0f, 0.0f, 1.0f, 0, true);

        GlStateManager.popMatrix();

        entityRenderer.enableLightmap();
    }
}
