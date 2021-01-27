package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.DeepLearnerGui;
import mustapelto.deepmoblearning.common.inventory.ContainerDeepLearner;
import mustapelto.deepmoblearning.common.items.DMLItem;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class DMLGuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == DMLConstants.GuiIDs.DEEP_LEARNER) {
            return new ContainerDeepLearner(player);
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack mainHand = player.getHeldItemMainhand();
        if (mainHand.getItem().getToolClasses(mainHand).contains("wrench")) {
            return null;
        }

        if (ID == DMLConstants.GuiIDs.DEEP_LEARNER) {
            return new DeepLearnerGui(player, world);
        }

        return null;
    }
}
