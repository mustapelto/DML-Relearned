package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber;
import mustapelto.deepmoblearning.common.tiles.TileEntitySimulationChamber.SimulationState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateSimChamber implements IMessage {
    private BlockPos pos;
    private int energy;
    private SimulationState simulationState;

    public MessageUpdateSimChamber() {}

    public MessageUpdateSimChamber(BlockPos pos, int energy, SimulationState simulationState) {
        this.pos = pos;
        this.energy = energy;
        this.simulationState = simulationState;
    }

    public MessageUpdateSimChamber(TileEntitySimulationChamber te) {
        this(te.getPos(), te.getEnergy(), te.getSimulationState());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(energy);
        buf.writeBytes(simulationState.toBytes());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        energy = buf.readInt();
        simulationState = new SimulationState().fromBytes(buf);
    }

    public static class Handler implements IMessageHandler<MessageUpdateSimChamber, IMessage> {
        @Override
        public IMessage onMessage(MessageUpdateSimChamber message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               TileEntitySimulationChamber te = (TileEntitySimulationChamber) Minecraft.getMinecraft().world.getTileEntity(message.pos);
               if (te != null) {
                   te.setState(message.energy, message.simulationState);
               }
            });
            return null;
        }
    }
}
