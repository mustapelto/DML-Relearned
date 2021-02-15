package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants.Gui.Colors;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonBase;
import mustapelto.deepmoblearning.client.gui.buttons.ButtonDeepLearnerSelect;
import mustapelto.deepmoblearning.common.inventory.ContainerDeepLearner;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static mustapelto.deepmoblearning.DMLConstants.Gui.DeepLearner.*;
import static mustapelto.deepmoblearning.DMLConstants.Gui.ROW_SPACING;

public class GuiDeepLearner extends GuiContainerBase {
    // STATE VARIABLES
    private final ItemStack deepLearner; // Deep Learner that opened this GUI
    private NonNullList<ItemStack> dataModels; // Contained Data Models
    private int currentModelIndex = 0; // Currently selected Model for display
    private ItemStack currentModelStack;
    private MetadataDataModel currentModelMetadata;
    private MetadataDataModel.DeepLearnerDisplayData currentDisplayData;

    private ImmutableList<ImmutablePair<String, Integer>> defaultStringList;

    private ButtonDeepLearnerSelect prevModelButton;
    private ButtonDeepLearnerSelect nextModelButton;

    //
    // INIT
    //

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
            currentModelMetadata = MetadataDataModel.INVALID;
            currentDisplayData = MetadataDataModel.DeepLearnerDisplayData.INVALID;
            setModelSelectButtonsEnabled(false);
            return;
        }

        // Invalid Model Index -> reset to 0
        if (currentModelIndex < 0 || currentModelIndex >= dataModels.size()) {
            currentModelIndex = 0;
        }

        currentModelStack = dataModels.get(currentModelIndex);
        currentModelMetadata = DataModelHelper.getDataModelMetadata(currentModelStack);
        currentDisplayData = currentModelMetadata.getDeepLearnerDisplayData();

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
                MAIN_GUI_TEXTURE_LOCATION.X,
                MAIN_GUI_TEXTURE_LOCATION.Y,
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
                MOB_DISPLAY_TEXTURE_LOCATION.X,
                MOB_DISPLAY_TEXTURE_LOCATION.Y,
                MOB_DISPLAY.WIDTH,
                MOB_DISPLAY.HEIGHT
        );

        if (currentModelStack.isEmpty()) {
            // No Data Models in Learner
            renderDisplayStrings(defaultStringList);
            return;
        }

        // At least 1 data model -> display metadata
        // Get and render main entity
        Entity mainEntity = currentDisplayData.getEntity(world);
        if (mainEntity != null) {
            renderEntity(
                    mainEntity,
                    currentDisplayData.getEntityScale(),
                    guiLeft + MOB_DISPLAY_ENTITY.X + currentDisplayData.getEntityOffsetX(),
                    guiTop + MOB_DISPLAY_ENTITY.Y + currentDisplayData.getEntityOffsetY(),
                    partialTicks
            );
        }

        // Get and render extra entity
        Entity extraEntity = currentDisplayData.getExtraEntity(world);
        if (extraEntity != null) {
            renderEntity(
                    extraEntity,
                    currentDisplayData.getEntityScale(),
                    guiLeft + MOB_DISPLAY_ENTITY.X + currentDisplayData.getExtraEntityOffsetX(),
                    guiTop + MOB_DISPLAY_ENTITY.Y + currentDisplayData.getExtraEntityOffsetY(),
                    partialTicks
            );
        }

        // Draw metadata text
        renderMetaData();
    }

    private void renderMetaData() {
        // Get data from Data Model ItemStack
        String dataModelTier = DataModelHelper.getTierDisplayNameFormatted(currentModelStack);
        String nextTier = DataModelHelper.getNextTierDisplayNameFormatted(currentModelStack);
        String mobName = currentModelMetadata.getDisplayName();
        String mobPluralName = currentModelMetadata.getDisplayNamePlural();
        ImmutableList<String> mobTrivia = currentDisplayData.getMobTrivia();

        int totalKills = DataModelHelper.getTotalKillCount(currentModelStack);
        int killsToNextTier = DataModelHelper.getKillsToNextTier(currentModelStack);
        String tierString = DataModelHelper.isAtMaxTier(currentModelStack) ?
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
                HEART_ICON_TEXTURE_LOCATION.X,
                HEART_ICON_TEXTURE_LOCATION.Y,
                HEART_ICON.WIDTH,
                HEART_ICON.HEIGHT
        );

        drawString(
                fontRenderer,
                I18n.format("deepmoblearning.deep_learner.health_points"),
                guiLeft + HEALTH_POINTS_HEADER_LOCATION.X,
                guiTop + HEALTH_POINTS_HEADER_LOCATION.Y,
                Colors.AQUA
        );

        // Obfuscate if hearts == 0 (for models with multiple mobs, e.g. Twilight Forest)
        drawString(
                fontRenderer,
                numHearts == 0 ?
                        TextFormatting.OBFUSCATED + "10" + TextFormatting.RESET :
                        Integer.toString(numHearts),
                guiLeft + HEALTH_POINTS_TEXT_LOCATION.X,
                guiTop + HEALTH_POINTS_TEXT_LOCATION.Y,
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

    private void renderEntity(Entity entity, int scale, int x, int y, float partialTicks) {
        EntityRenderer entityRenderer = mc.entityRenderer;

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
        mc.getRenderManager().renderEntity(entity, 0.0f, 0.0f, 0.0f, 1.0f, 0, true);

        GlStateManager.popMatrix();

        // Re-enable lightmap
        entityRenderer.enableLightmap();
    }
}
