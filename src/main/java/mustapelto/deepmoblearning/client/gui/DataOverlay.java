package mustapelto.deepmoblearning.client.gui;

import mustapelto.deepmoblearning.DMLConfig;
import mustapelto.deepmoblearning.DMLConfig.GuiOverlaySettings.GuiPosition;
import mustapelto.deepmoblearning.DMLConstants.GuiColors;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class DataOverlay extends GuiScreen {
    private static final int BAR_SPACING = 12;
    private static final int COMPONENT_HEIGHT = 26;
    private static final int DATA_MODEL_WIDTH = 18;
    private static final int EXP_BAR_MAX_WIDTH = 89;
    private static final int EXP_BAR_INNER_HEIGHT = 11;
    private static final int EXP_BAR_OUTER_HEIGHT = 12;
    private static final int PADDING_BASE_HORIZONTAL = 5;
    private static final int PADDING_BASE_VERTICAL = 5;

    private final Minecraft mc;
    private final FontRenderer fontRenderer;

    public DataOverlay(Minecraft mc) {
        this.mc = mc;
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
        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE)
            return;

        if (!mc.inGameHasFocus)
            return;

        EntityPlayer player = mc.player;
        ItemStack deepLearner = getHeldDeepLearner(player);

        if (deepLearner == null)
            return;

        NonNullList<ItemStack> dataModels = DataModelHelper.getDataModelStacksFromList(ItemDeepLearner.getContainedItems(deepLearner));

        int paddingHorizontal = DMLConfig.GUI_OVERLAY_SETTINGS.PADDING_HORIZONTAL;
        int paddingVertical = DMLConfig.GUI_OVERLAY_SETTINGS.PADDING_VERTICAL;
        GuiPosition position = DMLConfig.GUI_OVERLAY_SETTINGS.getGuiPosition();
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

    private ItemStack getHeldDeepLearner(EntityPlayer player) {
        ItemStack mainHandStack = player.getHeldItemMainhand();
        ItemStack offHandStack = player.getHeldItemOffhand();

        if (mainHandStack.getItem() instanceof ItemDeepLearner)
            return mainHandStack;
        else if (offHandStack.getItem() instanceof ItemDeepLearner)
            return offHandStack;

        return null;
    }

    private void drawExperienceBar(int x, int y, int index, ItemStack dataModel) {
        drawItemStack(x, y - 2 + BAR_SPACING + (index * COMPONENT_HEIGHT), dataModel);
        drawString(fontRenderer, dataModel.getDisplayName(), x + 4, y + (index * COMPONENT_HEIGHT) + 2, GuiColors.WHITE);

        mc.getTextureManager().bindTexture(GuiRegistry.DATA_OVERLAY.EXP_BAR);
        drawTexturedModalRect(x + DATA_MODEL_WIDTH, y + BAR_SPACING + (index * COMPONENT_HEIGHT), 0, 0, EXP_BAR_MAX_WIDTH, EXP_BAR_OUTER_HEIGHT);

        if (DataModelHelper.isAtMaxTier(dataModel)) {
            drawTexturedModalRect(x + DATA_MODEL_WIDTH + 1, y + 1 + BAR_SPACING + (index * COMPONENT_HEIGHT), 0, EXP_BAR_OUTER_HEIGHT, EXP_BAR_MAX_WIDTH, EXP_BAR_INNER_HEIGHT);
        } else {
            int dataCurrent = DataModelHelper.getCurrentTierDataCount(dataModel);
            int dataRequired = DataModelHelper.getTierRequiredData(dataModel);
            int killsRequired = DataModelHelper.getKillsToNextTier(dataModel);
            int barWidth = (int) (((float) dataCurrent / dataRequired) * EXP_BAR_MAX_WIDTH);
            drawTexturedModalRect(x + DATA_MODEL_WIDTH + 1, y + 1 + BAR_SPACING + (index * COMPONENT_HEIGHT), 0, EXP_BAR_OUTER_HEIGHT, barWidth, EXP_BAR_INNER_HEIGHT);
            drawString(fontRenderer, killsRequired + " kills needed", x + DATA_MODEL_WIDTH + 3, y + 2 + BAR_SPACING + (index * COMPONENT_HEIGHT), GuiColors.WHITE);
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
