package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.DeepLearnerGui;
import mustapelto.deepmoblearning.client.gui.SimulationChamberGui;
import mustapelto.deepmoblearning.common.inventory.ContainerDeepLearner;
import mustapelto.deepmoblearning.common.inventory.ContainerSimulationChamber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;
import java.util.Objects;

public class DMLGuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case DMLConstants.GuiIDs.DEEP_LEARNER:
                return new ContainerDeepLearner(player);
            case DMLConstants.GuiIDs.SIMULATION_CHAMBER:
                TileEntity simulationChamber = Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z)));
                return new ContainerSimulationChamber(simulationChamber, player.inventory);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack mainHand = player.getHeldItemMainhand();
        if (mainHand.getItem().getToolClasses(mainHand).contains("wrench")) {
            return null;
        }

        switch (ID) {
            case DMLConstants.GuiIDs.DEEP_LEARNER:
                return new DeepLearnerGui(player, world);
            case DMLConstants.GuiIDs.SIMULATION_CHAMBER:
                TileEntity simulationChamber = Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z)));
                return new SimulationChamberGui(simulationChamber, player, world);
            default:
                return null;
        }
    }
}
