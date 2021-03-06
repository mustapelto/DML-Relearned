package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLConstants.Gui.Colors;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.DMLConfig.DeepLearnerGuiOverlaySettings.GuiPosition;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.PlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import static mustapelto.deepmoblearning.DMLConstants.Gui.ROW_SPACING;

@EventBusSubscriber(Side.CLIENT)
public class GuiDeepLearnerOverlay extends GuiScreen {
    public static GuiDeepLearnerOverlay INSTANCE = new GuiDeepLearnerOverlay();

    private static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deep_learner_overlay.png");

    private static final int COMPONENT_HEIGHT = 26;
    private static final int DATA_MODEL_WIDTH = 18;
    private static final int EXP_BAR_MAX_WIDTH = 89;
    private static final int EXP_BAR_INNER_HEIGHT = 11;
    private static final int EXP_BAR_OUTER_HEIGHT = 12;
    private static final int PADDING_BASE_HORIZONTAL = 5;
    private static final int PADDING_BASE_VERTICAL = 5;

    private final Minecraft mc;
    private final FontRenderer fontRenderer;

    private GuiDeepLearnerOverlay() {
        mc = Minecraft.getMinecraft();
        fontRenderer = mc.fontRenderer;
        itemRender = mc.getRenderItem();
        setGuiSize(EXP_BAR_MAX_WIDTH, EXP_BAR_OUTER_HEIGHT);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (!mc.inGameHasFocus || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE)
            return;

        EntityPlayer player = mc.player;
        ItemStack deepLearner = PlayerHelper.getHeldDeepLearner(player);

        if (deepLearner.isEmpty())
            return;

        ImmutableList<ItemStack> dataModels = DataModelHelper.getDataModelStacksFromList(ItemDeepLearner.getContainedItems(deepLearner));

        int paddingHorizontal = DMLConfig.DEEP_LEARNER_GUI_OVERLAY_SETTINGS.PADDING_HORIZONTAL;
        int paddingVertical = DMLConfig.DEEP_LEARNER_GUI_OVERLAY_SETTINGS.PADDING_VERTICAL;
        GuiPosition position = DMLConfig.DEEP_LEARNER_GUI_OVERLAY_SETTINGS.getGuiPosition();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int numberOfBars = dataModels.size();
        int x;
        int y;

        switch (position) {
            case TOP_LEFT:
            default:
                x = PADDING_BASE_HORIZONTAL + paddingHorizontal;
                y = PADDING_BASE_VERTICAL + paddingVertical;
                break;
            case TOP_RIGHT:
                x = scaledResolution.getScaledWidth() - width - PADDING_BASE_HORIZONTAL - paddingHorizontal;
                y = PADDING_BASE_VERTICAL + paddingVertical;
                break;
            case BOTTOM_RIGHT:
                x = scaledResolution.getScaledWidth() - width - PADDING_BASE_HORIZONTAL - paddingHorizontal;
                y = scaledResolution.getScaledHeight() - (numberOfBars * COMPONENT_HEIGHT) - PADDING_BASE_VERTICAL - paddingVertical;
                break;
            case BOTTOM_LEFT:
                x = PADDING_BASE_HORIZONTAL + paddingHorizontal;
                y = scaledResolution.getScaledHeight() - (numberOfBars * COMPONENT_HEIGHT) - PADDING_BASE_VERTICAL - paddingVertical;
                break;
        }

        for (int i = 0; i < dataModels.size(); i++) {
            ItemStack dataModel = dataModels.get(i);
            drawExperienceBar(x, y, i, dataModel);
        }
    }

    private void drawExperienceBar(int x, int y, int index, ItemStack dataModel) {
        drawItemStack(x, y - 2 + ROW_SPACING + (index * COMPONENT_HEIGHT), dataModel);
        drawString(fontRenderer, dataModel.getDisplayName(), x + 4, y + (index * COMPONENT_HEIGHT) + 2, Colors.WHITE);

        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(x + DATA_MODEL_WIDTH, y + ROW_SPACING + (index * COMPONENT_HEIGHT), 0, 0, EXP_BAR_MAX_WIDTH, EXP_BAR_OUTER_HEIGHT);

        if (DataModelHelper.isMaxTier(dataModel)) {
            drawTexturedModalRect(x + DATA_MODEL_WIDTH + 1, y + 1 + ROW_SPACING + (index * COMPONENT_HEIGHT), 0, EXP_BAR_OUTER_HEIGHT, EXP_BAR_MAX_WIDTH, EXP_BAR_INNER_HEIGHT);
        } else {
            int dataCurrent = DataModelHelper.getCurrentTierDataCount(dataModel);
            int dataRequired = DataModelHelper.getTierRequiredData(dataModel);
            int killsRequired = DataModelHelper.getKillsToNextTier(dataModel);
            int barWidth = (int) (((float) dataCurrent / dataRequired) * EXP_BAR_MAX_WIDTH);
            drawTexturedModalRect(x + DATA_MODEL_WIDTH + 1, y + 1 + ROW_SPACING + (index * COMPONENT_HEIGHT), 0, EXP_BAR_OUTER_HEIGHT, barWidth, EXP_BAR_INNER_HEIGHT);
            drawString(fontRenderer, I18n.format("deepmoblearning.data_overlay.kills_needed", killsRequired), x + DATA_MODEL_WIDTH + 3, y + 2 + ROW_SPACING + (index * COMPONENT_HEIGHT), Colors.WHITE);
        }
    }

    private void drawItemStack(int x, int y, ItemStack stack) {
        GlStateManager.translate(0.0f, 0.0f, 32.0f);
        zLevel = 200.0f;
        itemRender.zLevel = 200.0f;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        zLevel = 0.0f;
        itemRender.zLevel = 0.0f;
    }
}
