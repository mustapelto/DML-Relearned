package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.tiles.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateTileEntity implements IMessage {
    private BlockPos pos;
    private ByteBuf payload;

    public MessageUpdateTileEntity() {}

    public MessageUpdateTileEntity(BlockPos pos, ByteBuf payload) {
        this.pos = pos;
        this.payload = payload;
    }

    public MessageUpdateTileEntity(TileEntityBase te) {
        this(te.getPos(), te.getUpdateData());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeBytes(payload);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        buf.readBytes(payload);
    }

    public static class Handler implements IMessageHandler<MessageUpdateTileEntity, IMessage> {
        @Override
        public IMessage onMessage(MessageUpdateTileEntity message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               TileEntityBase te = (TileEntityBase) Minecraft.getMinecraft().world.getTileEntity(message.pos);
               if (te != null) {
                   te.handleUpdateData(message.payload);
               }
            });
            return null;
        }
    }
}
