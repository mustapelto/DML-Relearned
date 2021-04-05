package mustapelto.deepmoblearning.client.gui;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.capabilities.CapabilityPlayerTrial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(Side.CLIENT)
public class GuiTrialOverlay extends GuiScreen {
    public static GuiTrialOverlay INSTANCE = new GuiTrialOverlay();

    private static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/trial_overlay.png");

    private static final int GUI_WIDTH = 89;
    private static final int GUI_HEIGHT = 12;
    private static final int GLITCH_FACE_SIZE = 17;

    private final Minecraft mc;
    private final FontRenderer fontRenderer;

    private final List<OverlayMessage> messages = new ArrayList<>();

    private CapabilityPlayerTrial playerCapability;

    private GuiTrialOverlay() {
        mc = Minecraft.getMinecraft();
        fontRenderer = mc.fontRenderer;
        itemRender = mc.getRenderItem();
        setGuiSize(GUI_WIDTH, GUI_HEIGHT);
    }

    public void setPlayerCapability() {
        playerCapability = (CapabilityPlayerTrial) DMLRelearned.proxy.getClientPlayerTrialCapability();
    }

    public void addMessage(OverlayMessage message) {
        messages.add(message);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        Iterator<OverlayMessage> it = messages.iterator();
        while (it.hasNext()) {
            OverlayMessage message = it.next();
            message.tick();
            if (message.finished())
                it.remove();
        }
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

        messages.forEach(message -> {
           switch (message) {
               case ABORTED:
               case COMPLETED:
               case WAVE:
                   renderMessage(message.getMessage());
                   break;
               case GLITCH:
                   renderGlitchWarning();
               case COUNTDOWN:
                   renderCountdown(message.ticksLeft);
           }
        });
    }

    private void renderMessage(String message) {

    }

    private void renderGlitchWarning() {

    }

    private void renderCountdown(int ticksLeft) {

    }

    public enum OverlayMessage {
        ABORTED(80) {
            @Override
            public String getMessage() {
                return I18n.format("deepmoblearning.trial.message.aborted");
            }
        },
        COMPLETED(120) {
            @Override
            public String getMessage() {
                return I18n.format("deepmoblearning.trial.message.completed");
            }
        },
        WAVE(80) {
            @Override
            public String getMessage() {
                return I18n.format("deepmoblearning.trial.message.wave");
            }
        },
        GLITCH(80),
        COUNTDOWN(120);

        private static final ImmutableList<OverlayMessage> values = ImmutableList.copyOf(values());
        private int ticksLeft;

        OverlayMessage(int ticksLeft) {
            this.ticksLeft = ticksLeft;
        }

        public void tick() {
            ticksLeft--;
        }

        public boolean finished() {
            return ticksLeft <= 0;
        }

        public String getMessage() {
            return "";
        }

        public int getIndex() {
            return values.indexOf(this);
        }

        public static OverlayMessage byIndex(int index) {
            if (index < 0 || index >= values.size())
                return ABORTED;

            return values.get(index);
        }
    }
}
