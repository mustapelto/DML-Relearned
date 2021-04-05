package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.capabilities.CapabilityPlayerTrial;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageTrialCapabilityData implements IMessage {
    private ByteBuf data;

    public MessageTrialCapabilityData() {}

    public MessageTrialCapabilityData(CapabilityPlayerTrial cap) {
        data = Unpooled.buffer();
        cap.toBytes(data);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBytes(data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = buf.copy();
    }

    public static class Handler implements IMessageHandler<MessageTrialCapabilityData, IMessage> {
        @Override
        @Nullable
        public IMessage onMessage(MessageTrialCapabilityData message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                CapabilityPlayerTrial clientCapability = (CapabilityPlayerTrial) DMLRelearned.proxy.getClientPlayerTrialCapability();

                if (clientCapability != null)
                    clientCapability.fromBytes(message.data);
            });
            return null;
        }
    }
}
