package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLConstants.Gui.Colors;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.common.inventory.ContainerDeepLearner;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.metadata.MobMetadata;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.io.IOException;

import static mustapelto.deepmoblearning.DMLConstants.Gui.ROW_SPACING;

public class GuiDeepLearner extends GuiContainerBase {
    // TODO: Implement buttons as GuiButtons
    // GUI Textures
    public static final ResourceLocation BASE_TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deep_learner_base.png");
    public static final ResourceLocation EXTRAS_TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deep_learner_extras.png");

    // GUI Size
    public static final int WIDTH = 338;
    public static final int HEIGHT = 235;

    private final ItemStack deepLearner; // Deep Learner that opened this GUI
    private NonNullList<ItemStack> dataModels; // Contained Data Models
    private int currentModelIndex = 0; // Currently selected Model for display

    private static final Rect BUTTON_PREV = new Rect(-27, 105, 24, 24);
    private static final Rect BUTTON_NEXT = new Rect(-1, 105, 24, 24);

    public GuiDeepLearner(EntityPlayer player, World world) {
        super(player, world, new ContainerDeepLearner(player), WIDTH, HEIGHT);

        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        if (mainHand.getItem() instanceof ItemDeepLearner)
            this.deepLearner = mainHand;
        else if (offHand.getItem() instanceof ItemDeepLearner)
            this.deepLearner = offHand;
        else
            throw new IllegalStateException("Tried to open Deep Learner GUI without Deep Learner equipped");
    }

    private int nextItemIndex() {
        return currentModelIndex == dataModels.size() - 1 ? 0 : currentModelIndex + 1;
    }

    private int prevItemIndex() {
        return currentModelIndex == 0 ? dataModels.size() - 1 : currentModelIndex - 1;
    }

    @Override
    protected void initButtons() {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        if (dataModels.size() > 1) {
            if (BUTTON_PREV.isInside(x, y)) {
                currentModelIndex = prevItemIndex();
            } else if (BUTTON_NEXT.isInside(x, y)) {
                currentModelIndex = nextItemIndex();
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleButtonPress(ButtonBase button, int mouseButton) {

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        final int left = guiLeft;
        final int top = guiTop;

        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        // Draw main GUI
        textureManager.bindTexture(BASE_TEXTURE);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawTexturedModalRect(left + 41, top, 0, 0, 256, 140);

        // Draw player inventory
        drawPlayerInventory(guiLeft + 81, guiTop + 145);

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
            MobMetadata mobMetaData = DataModelHelper.getMobMetadata(currentModelStack);

            if (mobMetaData == null) {
                return;
            }

            renderMetaData(textureManager, mobMetaData, left, top, currentModelStack);
            renderMobDisplayBox(textureManager, left, top);

            // Get and render main entity
            Entity mainEntity = mobMetaData.getEntity(world);
            if (mainEntity != null) {
                renderEntity(mainEntity,
                        mobMetaData.getDisplayEntityScale(),
                        left + mobMetaData.getDisplayEntityOffsetX(),
                        top + 80 + mobMetaData.getDisplayEntityOffsetY(),
                        partialTicks);
            }

            // Get and render extra entity
            Entity extraEntity = mobMetaData.getExtraEntity(world);
            if (extraEntity != null) {
                renderEntity(extraEntity,
                        mobMetaData.getDisplayEntityScale(),
                        left + mobMetaData.getDisplayExtraEntityOffsetX(),
                        top + 80 + mobMetaData.getDisplayExtraEntityOffsetY(),
                        partialTicks);
            }
        } else {
            // No Data Models in Learner
            renderDefaultScreen(left, top);
        }
    }

    private void renderCycleButtons(TextureManager textureManager, int left, int top, int mouseX, int mouseY) {
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        // Draw buttons
        textureManager.bindTexture(EXTRAS_TEXTURE);
        drawTexturedModalRect(left + BUTTON_PREV.LEFT, top + BUTTON_PREV.TOP, 75, 0, BUTTON_PREV.WIDTH, BUTTON_PREV.HEIGHT);
        drawTexturedModalRect(left + BUTTON_NEXT.LEFT, top + BUTTON_NEXT.TOP, 99, 0, BUTTON_NEXT.WIDTH, BUTTON_NEXT.HEIGHT);

        // Hover states
        if (BUTTON_PREV.isInside(x, y)) {
            drawTexturedModalRect(left + BUTTON_PREV.LEFT, top + BUTTON_PREV.TOP, 75, 24, BUTTON_PREV.WIDTH, BUTTON_PREV.HEIGHT);
        } else if (BUTTON_NEXT.isInside(x, y)) {
            drawTexturedModalRect(left + BUTTON_NEXT.LEFT, top + BUTTON_NEXT.TOP, 99, 24, BUTTON_NEXT.WIDTH, BUTTON_NEXT.HEIGHT);
        }
    }

    private void renderDefaultScreen(int left, int top) {
        int leftStart = left + 49;
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_1"), leftStart, top + ROW_SPACING, Colors.AQUA);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_2"), leftStart, top + 2 * ROW_SPACING, Colors.WHITE);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_3"), leftStart, top + 3 * ROW_SPACING, Colors.WHITE);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_4"), leftStart, top + 4 * ROW_SPACING, Colors.WHITE);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_5"), leftStart, top + 6 * ROW_SPACING, Colors.WHITE);
        drawString(fontRenderer, I18n.format("deepmoblearning.deep_learner.no_model.line_6"), leftStart, top + 7 * ROW_SPACING, Colors.WHITE);
    }

    private void renderMetaData(TextureManager textureManager, MobMetadata mobMetaData, int left, int top, ItemStack stack) {
        int leftStart = left + 49;
        int topStart = top - 4;

        // Get data from Data Model ItemStack
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
                Colors.AQUA);

        drawString(fontRenderer,
                mobName,
                leftStart,
                topStart + (2 * ROW_SPACING),
                Colors.WHITE);

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.heading_information"),
                leftStart,
                topStart + (3 * ROW_SPACING),
                Colors.AQUA);

        for (int i = 0; i < mobTrivia.length; i++) {
            drawString(fontRenderer,
                    mobTrivia[i],
                    leftStart,
                    topStart + (3 * ROW_SPACING) + ((i + 1) * 12),
                    Colors.WHITE);
        }

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.model_tier", dataModelTier),
                leftStart,
                topStart + (8 * ROW_SPACING),
                Colors.WHITE);

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.defeated", mobPluralName, totalKills),
                leftStart,
                topStart + (9 * ROW_SPACING),
                Colors.WHITE);

        drawString(fontRenderer,
                DataModelHelper.isAtMaxTier(stack) ?
                        I18n.format("deepmoblearning.deep_learner.maximum") :
                        I18n.format("deepmoblearning.deep_learner.required", killsToNextTier, nextTier),
                leftStart,
                topStart + (10 * ROW_SPACING),
                Colors.WHITE);

        // Draw heart
        textureManager.bindTexture(BASE_TEXTURE);
        drawTexturedModalRect(left + 228, topStart + (2 * ROW_SPACING) - 2, 0, 140, 9, 9);

        drawString(fontRenderer,
                I18n.format("deepmoblearning.deep_learner.health_points"),
                left + 228,
                topStart + ROW_SPACING,
                Colors.AQUA);

        // Obfuscate if hearts == 0 (for models with multiple mobs, e.g. Twilight Forest)
        drawString(fontRenderer,
                numHearts == 0 ?
                        TextFormatting.OBFUSCATED + "10" + TextFormatting.RESET :
                        Integer.toString(numHearts),
                left + 239,
                topStart + (2 * ROW_SPACING) - 1,
                Colors.WHITE
                );
    }

    private void renderMobDisplayBox(TextureManager textureManager, int left, int top) {
        textureManager.bindTexture(EXTRAS_TEXTURE);
        drawTexturedModalRect(left - 41, top, 0, 0, 75, 101);
    }

    private void renderEntity(Entity entity, int scale, int x, int y, float partialTicks) {
        EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;

        // Don't need lightmap for GUI rendering
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

        // Re-enable lightmap
        entityRenderer.enableLightmap();
    }
}
