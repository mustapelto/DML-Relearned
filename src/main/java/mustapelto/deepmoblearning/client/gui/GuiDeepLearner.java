package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLConstants.Gui.Colors;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonDeepLearnerSelect;
import mustapelto.deepmoblearning.common.inventory.ContainerDeepLearner;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.PlayerHelper;
import mustapelto.deepmoblearning.common.util.Point;
import mustapelto.deepmoblearning.common.util.Rect;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nullable;

import static mustapelto.deepmoblearning.DMLConstants.Gui.DeepLearner.PLAYER_INVENTORY;
import static mustapelto.deepmoblearning.DMLConstants.Gui.ROW_SPACING;

public class GuiDeepLearner extends GuiContainerBase {
    // TEXTURE
    private static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deep_learner.png");
    private static final class TextureCoords {
        private static final Point MAIN_GUI = new Point(0, 0);
        private static final Point MOB_DISPLAY = new Point(0, 140);
        private static final Point HEART_ICON = new Point(75, 140);
    }

    // DIMENSIONS
    private static final int WIDTH = 338;
    private static final int HEIGHT = 235;
    private static final Rect MAIN_GUI = new Rect(41, 0, 256, 140);

    // MOB DISPLAY
    private static final Rect MOB_DISPLAY = new Rect(-41, 0, 75, 101);
    private static final Point MOB_DISPLAY_ENTITY = new Point(0, 80);

    // MAIN DISPLAY
    private static final Point TEXT_START = new Point(49, 8);
    private static final Rect HEART_ICON = new Rect(228, 2 * ROW_SPACING - 6, 9, 9);
    private static final Point HEALTH_POINTS_HEADER = new Point(228, ROW_SPACING - 4);
    private static final Point HEALTH_POINTS_TEXT = new Point(239, 2 * ROW_SPACING - 4);

    // BUTTONS
    private static final Point PREV_MODEL_BUTTON = new Point(-27, 105);
    private static final Point NEXT_MODEL_BUTTON = new Point(-1, 105);

    // STATE VARIABLES
    private final ItemStack deepLearner; // Deep Learner that opened this GUI

    private ImmutableList<ItemStack> dataModels; // Contained Data Models
    private int currentModelIndex = 0; // Currently selected Model for display
    private ItemStack currentModelStack;

    @Nullable
    private MetadataDataModel currentModelMetadata;

    @Nullable
    private MetadataDataModel.DeepLearnerDisplayData currentDisplayData;

    private ImmutableList<ImmutablePair<String, Integer>> defaultStringList;

    private ButtonDeepLearnerSelect prevModelButton;
    private ButtonDeepLearnerSelect nextModelButton;

    private float currentEntityRotation = 0;
    private double currentEntityHeight = 0;

    //
    // INIT
    //

    public GuiDeepLearner(EntityPlayer player, World world) {
        super(player, world, new ContainerDeepLearner(player), WIDTH, HEIGHT);

        deepLearner = PlayerHelper.getHeldDeepLearner(player);
        if (deepLearner.isEmpty())
            throw new IllegalStateException("Tried to open Deep Learner GUI without Deep Learner equipped");
    }

    @Override
    public void initGui() {
        super.initGui();
        initDefaultStringList();
        updateDisplayData();
    }

    private void initDefaultStringList() {
        ImmutableList.Builder<ImmutablePair<String, Integer>> builder = ImmutableList.builder();

        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.no_model.line_1"), Colors.AQUA));
        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.no_model.line_2"), Colors.WHITE));
        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.no_model.line_3"), Colors.WHITE));
        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.no_model.line_4"), Colors.WHITE));
        builder.add(ImmutablePair.of("", 0));
        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.no_model.line_5"), Colors.WHITE));
        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.no_model.line_6"), Colors.WHITE));

        defaultStringList = builder.build();
    }

    //
    // UPDATE
    //


    @Override
    public void updateScreen() {
        super.updateScreen();
        updateDisplayData();
    }

    private void updateDisplayData() {
        NonNullList<ItemStack> deepLearnerStackList = ItemDeepLearner.getContainedItems(deepLearner);
        dataModels = DataModelHelper.getDataModelStacksFromList(deepLearnerStackList);

        if (dataModels.isEmpty()) {
            currentModelIndex = -1;
            currentModelStack = ItemStack.EMPTY;
            currentModelMetadata = null;
            currentDisplayData = null;
            setModelSelectButtonsEnabled(false);
            return;
        }

        // Invalid Model Index -> reset to 0
        if (currentModelIndex < 0 || currentModelIndex >= dataModels.size()) {
            currentModelIndex = 0;
        }

        currentModelStack = dataModels.get(currentModelIndex);
        currentModelMetadata = DataModelHelper.getDataModelMetadata(currentModelStack).orElse(null);
        currentDisplayData = currentModelMetadata != null ? currentModelMetadata.getDeepLearnerDisplayData() : null;

        setModelSelectButtonsEnabled(dataModels.size() > 1);
    }

    //
    // BUTTONS
    //
    @Override
    protected void initButtons() {
        prevModelButton = new ButtonDeepLearnerSelect(0, guiLeft + PREV_MODEL_BUTTON.X, guiTop + PREV_MODEL_BUTTON.Y, ButtonDeepLearnerSelect.Direction.PREV);
        nextModelButton = new ButtonDeepLearnerSelect(0, guiLeft + NEXT_MODEL_BUTTON.X, guiTop + NEXT_MODEL_BUTTON.Y, ButtonDeepLearnerSelect.Direction.NEXT);
    }

    @Override
    protected void rebuildButtonList() {
        super.rebuildButtonList();

        buttonList.add(prevModelButton);
        buttonList.add(nextModelButton);
    }

    @Override
    protected void handleButtonPress(ButtonBase button, int mouseButton) {
        if (mouseButton == 0 && button instanceof ButtonDeepLearnerSelect) {
            ButtonDeepLearnerSelect modelSelectButton = (ButtonDeepLearnerSelect) button;
            if (modelSelectButton.getDirection() == ButtonDeepLearnerSelect.Direction.PREV) {
                currentModelIndex--;
                if (currentModelIndex < 0)
                    currentModelIndex = dataModels.size() - 1;
            } else {
                currentModelIndex++;
                if (currentModelIndex >= dataModels.size())
                    currentModelIndex = 0;
            }
        }
    }

    private void setModelSelectButtonsEnabled(boolean enabled) {
        prevModelButton.enabled = enabled;
        nextModelButton.enabled = enabled;
    }

    //
    // DRAWING
    //

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Draw main GUI
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        textureManager.bindTexture(TEXTURE);
        drawTexturedModalRect(
                guiLeft + MAIN_GUI.LEFT,
                guiTop + MAIN_GUI.TOP,
                TextureCoords.MAIN_GUI.X,
                TextureCoords.MAIN_GUI.Y,
                MAIN_GUI.WIDTH,
                MAIN_GUI.HEIGHT
        );

        // Draw player inventory
        drawPlayerInventory(guiLeft + PLAYER_INVENTORY.X, guiTop + PLAYER_INVENTORY.Y);

        // Draw mob display box
        textureManager.bindTexture(TEXTURE);
        drawTexturedModalRect(
                guiLeft + MOB_DISPLAY.LEFT,
                guiTop + MOB_DISPLAY.TOP,
                TextureCoords.MOB_DISPLAY.X,
                TextureCoords.MOB_DISPLAY.Y,
                MOB_DISPLAY.WIDTH,
                MOB_DISPLAY.HEIGHT
        );

        if (currentModelStack.isEmpty()) {
            // No Data Models in Learner
            renderDisplayStrings(defaultStringList);
            return;
        }

        // At least 1 data model -> display metadata
        if (currentDisplayData == null)
            return;

        // Draw metadata text
        renderMetaData();

        // Entity "bobbing" offset
        currentEntityHeight = Math.sin(lastRedrawTime / 16) / 8;

        // Entity rotating angle
        currentEntityRotation += deltaTime * 3;

        GlStateManager.pushAttrib();

        // Get and render main entity
        currentDisplayData.getEntity(world)
                .ifPresent(entity -> renderEntity(
                        entity,
                        currentDisplayData.getEntityScale(),
                        guiLeft + MOB_DISPLAY_ENTITY.X + currentDisplayData.getEntityOffsetX(),
                        guiTop + MOB_DISPLAY_ENTITY.Y + currentDisplayData.getEntityOffsetY()
                ));

        // Get and render extra entity
        currentDisplayData.getExtraEntity(world)
                .ifPresent(entity -> renderEntity(
                        entity,
                        currentDisplayData.getEntityScale(),
                        guiLeft + MOB_DISPLAY_ENTITY.X + currentDisplayData.getExtraEntityOffsetX(),
                        guiTop + MOB_DISPLAY_ENTITY.Y + currentDisplayData.getExtraEntityOffsetY()
                ));

        GlStateManager.popAttrib();
    }

    private void renderMetaData() {
        if (currentModelMetadata == null || currentDisplayData == null)
            return;

        // Get data from Data Model ItemStack
        String dataModelTier = DataModelHelper.getTierDisplayNameFormatted(currentModelStack);
        String nextTier = DataModelHelper.getNextTierDisplayNameFormatted(currentModelStack);
        String mobName = currentModelMetadata.getDisplayName();
        String mobPluralName = currentModelMetadata.getDisplayNamePlural();
        ImmutableList<String> mobTrivia = currentDisplayData.getMobTrivia();

        int totalKills = DataModelHelper.getTotalKillCount(currentModelStack);
        int killsToNextTier = DataModelHelper.getKillsToNextTier(currentModelStack);
        String tierString = DataModelHelper.isMaxTier(currentModelStack) ?
                I18n.format("deepmoblearning.deep_learner.maximum") :
                I18n.format("deepmoblearning.deep_learner.required", killsToNextTier, nextTier);

        int numHearts = currentDisplayData.getHearts();

        ImmutableList.Builder<ImmutablePair<String, Integer>> builder = ImmutableList.builder();

        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.heading_name"), Colors.AQUA));
        builder.add(ImmutablePair.of(mobName, Colors.WHITE));
        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.heading_information"), Colors.AQUA));
        for (String triviaLine : mobTrivia)
            builder.add(ImmutablePair.of(triviaLine, Colors.WHITE));
        for (int i = 0; i < 7 - (3 + mobTrivia.size()); i++)
            builder.add(ImmutablePair.of("", 0));
        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.model_tier", dataModelTier), Colors.WHITE));
        builder.add(ImmutablePair.of(I18n.format("deepmoblearning.deep_learner.defeated", mobPluralName, totalKills), Colors.WHITE));
        builder.add(ImmutablePair.of(tierString, Colors.WHITE));

        renderDisplayStrings(builder.build());

        // Draw heart
        textureManager.bindTexture(TEXTURE); // rebind after string drawing
        drawTexturedModalRect(
                guiLeft + HEART_ICON.LEFT,
                guiTop + HEART_ICON.TOP,
                TextureCoords.HEART_ICON.X,
                TextureCoords.HEART_ICON.Y,
                HEART_ICON.WIDTH,
                HEART_ICON.HEIGHT
        );

        drawString(
                fontRenderer,
                I18n.format("deepmoblearning.deep_learner.health_points"),
                guiLeft + HEALTH_POINTS_HEADER.X,
                guiTop + HEALTH_POINTS_HEADER.Y,
                Colors.AQUA
        );

        // Obfuscate if hearts == 0 (for models with multiple mobs, e.g. Twilight Forest)
        drawString(
                fontRenderer,
                numHearts == 0 ?
                        TextFormatting.OBFUSCATED + "10" + TextFormatting.RESET :
                        Integer.toString(numHearts),
                guiLeft + HEALTH_POINTS_TEXT.X,
                guiTop + HEALTH_POINTS_TEXT.Y,
                Colors.WHITE
        );
    }

    private void renderDisplayStrings(ImmutableList<ImmutablePair<String, Integer>> stringList) {
        for (int i = 0; i < stringList.size(); i++) {
            String string = stringList.get(i).left;
            if (!string.isEmpty())
                drawString(
                        fontRenderer,
                        string,
                        guiLeft + TEXT_START.X,
                        guiTop + TEXT_START.Y + i * ROW_SPACING,
                        stringList.get(i).right
                );
        }
    }

    private void renderEntity(Entity entity, int scale, int x, int y) {
        // TODO: Remove "magic numbers"
        EntityRenderer entityRenderer = mc.entityRenderer;

        // Don't need lightmap for GUI rendering
        entityRenderer.disableLightmap();

        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);

        // High z axis value to prevent clipping
        GlStateManager.translate(0.2f, currentEntityHeight, 15.0f);
        GlStateManager.rotate(currentEntityRotation, 0.0f, 1.0f, 0.0f);

        // Render entity
        mc.getRenderManager().renderEntity(entity, 0.0f, 0.0f, 0.0f, 1.0f, 0, true);

        GlStateManager.popMatrix();

        // Re-enable lightmap
        entityRenderer.enableLightmap();
    }
}
