package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.tiles.TileEntityBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestUpdateTileEntity implements IMessage {
    private BlockPos pos;
    private int dimension;

    public MessageRequestUpdateTileEntity() {}

    public MessageRequestUpdateTileEntity(BlockPos pos, int dimension) {
        this.pos = pos;
        this.dimension = dimension;
    }

    public MessageRequestUpdateTileEntity(TileEntityBase te) {
        this(te.getPos(), te.getWorld().provider.getDimension());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageRequestUpdateTileEntity, IMessage> {
        @Override
        public IMessage onMessage(MessageRequestUpdateTileEntity message, MessageContext ctx) {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            TileEntityBase te = (TileEntityBase) world.getTileEntity(message.pos);
            return (te != null) ? new MessageUpdateTileEntity(te) : null;
        }
    }
}
