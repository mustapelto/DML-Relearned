package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.tiles.RedstoneMode;
import mustapelto.deepmoblearning.common.tiles.TileEntityMachine;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRedstoneModeToServer implements IMessage {
    private BlockPos pos;
    private int dimension;
    private RedstoneMode redstoneMode;

    public MessageRedstoneModeToServer() {}

    public MessageRedstoneModeToServer(BlockPos pos, int dimension, RedstoneMode redstoneMode) {
        this.pos = pos;
        this.dimension = dimension;
        this.redstoneMode = redstoneMode;
    }

    public MessageRedstoneModeToServer(TileEntityMachine te) {
        this(te.getPos(), te.getWorld().provider.getDimension(), te.getRedstoneMode());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
        buf.writeInt(redstoneMode.getIndex());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
        redstoneMode = RedstoneMode.byIndex(buf.readInt());
    }

    public static class Handler implements IMessageHandler<MessageRedstoneModeToServer, IMessage> {
        @Override
        public IMessage onMessage(MessageRedstoneModeToServer message, MessageContext ctx) {
            WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            world.addScheduledTask(() -> {
                TileEntityMachine te = (TileEntityMachine) world.getTileEntity(message.pos);
                if (te != null) {
                    te.setRedstoneMode(message.redstoneMode);
                }
            });
            return null;
        }
    }
}
