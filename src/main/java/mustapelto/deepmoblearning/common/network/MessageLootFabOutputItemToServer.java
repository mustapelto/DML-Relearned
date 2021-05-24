package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.tiles.TileEntityLootFabricator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageLootFabOutputItemToServer implements IMessage {
    private BlockPos pos;
    private int dimension;
    private ItemStack outputItem;

    public MessageLootFabOutputItemToServer() {}

    public MessageLootFabOutputItemToServer(BlockPos pos, int dimension, ItemStack outputItem) {
        this.pos = pos;
        this.dimension = dimension;
        this.outputItem = outputItem;
    }

    public MessageLootFabOutputItemToServer(TileEntityLootFabricator target, ItemStack outputItem) {
        this(target.getPos(), target.getWorld().provider.getDimension(), outputItem);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
        ByteBufUtils.writeItemStack(buf, outputItem);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
        outputItem = ByteBufUtils.readItemStack(buf);
    }

    public static class Handler implements IMessageHandler<MessageLootFabOutputItemToServer, IMessage> {
        @Override
        @Nullable
        public IMessage onMessage(MessageLootFabOutputItemToServer message, MessageContext ctx) {
            WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            world.addScheduledTask(() -> {
                TileEntityLootFabricator te = (TileEntityLootFabricator) world.getTileEntity(message.pos);
                if (te != null) {
                    te.setOutputItem(message.outputItem);
                }
            });
            return null;
        }
    }
}
