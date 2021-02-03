package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.tiles.TileEntityRedstoneControlled;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRedstonePowerToClient implements IMessage {
    private BlockPos pos;
    private boolean redstonePowered;

    public MessageRedstonePowerToClient() {}

    public MessageRedstonePowerToClient(BlockPos pos, boolean redstonePowered) {
        this.pos = pos;
        this.redstonePowered = redstonePowered;
    }

    public MessageRedstonePowerToClient(TileEntityRedstoneControlled te) {
        this(te.getPos(), te.isRedstonePowered());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeBoolean(redstonePowered);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        redstonePowered = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<MessageRedstonePowerToClient, IMessage> {
        @Override
        public IMessage onMessage(MessageRedstonePowerToClient message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                TileEntityRedstoneControlled te = (TileEntityRedstoneControlled) Minecraft.getMinecraft().world.getTileEntity(message.pos);
                if (te != null) {
                    te.setRedstonePowered(message.redstonePowered);
                }
            });
            return null;
        }
    }
}
