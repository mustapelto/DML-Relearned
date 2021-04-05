package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.tiles.TileEntityLootFabricator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageLootFabOutputItem implements IMessage {
    private BlockPos pos;
    private int dimension;
    private int outputItemIndex;

    public MessageLootFabOutputItem() {}

    public MessageLootFabOutputItem(BlockPos pos, int dimension, int outputItemIndex) {
        this.pos = pos;
        this.dimension = dimension;
        this.outputItemIndex = outputItemIndex;
    }

    public MessageLootFabOutputItem(TileEntityLootFabricator target, int outputItemIndex) {
        this(target.getPos(), target.getWorld().provider.getDimension(), outputItemIndex);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
        buf.writeInt(outputItemIndex);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
        outputItemIndex = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageLootFabOutputItem, IMessage> {
        @Override
        @Nullable
        public IMessage onMessage(MessageLootFabOutputItem message, MessageContext ctx) {
            WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            world.addScheduledTask(() -> {
                TileEntityLootFabricator te = (TileEntityLootFabricator) world.getTileEntity(message.pos);
                if (te != null) {
                    te.setOutputItemIndex(message.outputItemIndex);
                }
            });
            return null;
        }
    }
}
