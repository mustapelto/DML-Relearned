package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateSimChamber implements IMessage {
    private BlockPos pos;
    private int energy;
    private boolean simulationRunning;
    private int simulationProgress;
    private boolean pristineSuccess;

    public MessageUpdateSimChamber() {}

    public MessageUpdateSimChamber(BlockPos pos, int energy, boolean simulationRunning, int simulationProgress, boolean pristineSuccess) {
        this.pos = pos;
        this.energy = energy;
        this.simulationRunning = simulationRunning;
        this.simulationProgress = simulationProgress;
        this.pristineSuccess = pristineSuccess;
    }

    public MessageUpdateSimChamber(TileEntitySimulationChamber te) {
        this(te.getPos(), te.getEnergy(), te.isSimulationRunning(), te.getSimulationProgress(), te.isPristineSuccess());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(energy);
        buf.writeBoolean(simulationRunning);
        buf.writeInt(simulationProgress);
        buf.writeBoolean(pristineSuccess);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        energy = buf.readInt();
        simulationRunning = buf.readBoolean();
        simulationProgress = buf.readInt();
        pristineSuccess = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<MessageUpdateSimChamber, IMessage> {
        @Override
        public IMessage onMessage(MessageUpdateSimChamber message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               TileEntitySimulationChamber te = (TileEntitySimulationChamber) Minecraft.getMinecraft().world.getTileEntity(message.pos);
               if (te != null) {
                   te.setState(message.energy, message.simulationRunning, message.simulationProgress, message.pristineSuccess);
               }
            });
            return null;
        }
    }
}
