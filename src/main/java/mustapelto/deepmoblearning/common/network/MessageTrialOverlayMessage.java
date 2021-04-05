package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.client.gui.GuiTrialOverlay;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageTrialOverlayMessage implements IMessage {
    private GuiTrialOverlay.OverlayMessage message;

    public MessageTrialOverlayMessage() {}

    public MessageTrialOverlayMessage(GuiTrialOverlay.OverlayMessage message) {
        this.message = message;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(message.getIndex());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        message = GuiTrialOverlay.OverlayMessage.byIndex(buf.readInt());
    }

    public static class Handler implements IMessageHandler<MessageTrialOverlayMessage, IMessage> {
        @Override
        @Nullable
        public IMessage onMessage(MessageTrialOverlayMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiTrialOverlay.INSTANCE.addMessage(message.message);
            });
            return null;
        }
    }
}
