package mustapelto.deepmoblearning.common.network;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public class DMLPacketHandler {
    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(DMLConstants.ModInfo.ID);
    private static int id = 0;

    public static void registerPackets() {
        network.registerMessage(MessageLivingMatterConsume.Handler.class, MessageLivingMatterConsume.class, id++, Side.SERVER);
        network.registerMessage(MessageLevelUpModel.Handler.class, MessageLevelUpModel.class, id++, Side.SERVER);
        network.registerMessage(MessageUpdateSimChamber.Handler.class, MessageUpdateSimChamber.class, id++, Side.CLIENT);
        network.registerMessage(MessageRequestUpdateSimChamber.Handler.class, MessageRequestUpdateSimChamber.class, id++, Side.SERVER);
        network.registerMessage(MessageRedstonePowerToClient.Handler.class, MessageRedstonePowerToClient.class, id++, Side.CLIENT);
        network.registerMessage(MessageRedstoneModeToServer.Handler.class, MessageRedstoneModeToServer.class, id++, Side.SERVER);
    }

    public static void sendToClient(IMessage message, World world, BlockPos pos) {
        network.sendToAllTracking(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
    }

    public static void sendToServer(IMessage message) {
        network.sendToServer(message);
    }
}
