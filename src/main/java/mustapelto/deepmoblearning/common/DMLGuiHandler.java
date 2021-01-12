package mustapelto.deepmoblearning.common;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.gui.DeepLearnerGui;
import mustapelto.deepmoblearning.common.inventory.ContainerDeepLearner;
import mustapelto.deepmoblearning.common.items.IGuiItem;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class DMLGuiHandler implements IGuiHandler {
    public static void openItemGui(EntityPlayer player, EntityEquipmentSlot slot) {
        ItemStack stack = player.getItemStackFromSlot(slot);
        Item item = stack.getItem();

        if (stack.isEmpty() || !(item instanceof IGuiItem))
            return;

        int slotID = (slot == EntityEquipmentSlot.MAINHAND) ? 0 : 1;

        player.openGui(DMLRelearned.instance, 100 * slotID + ((IGuiItem) item).getGuiID(), player.world, 0, 0, 0);
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // Find triggering slot (offhand vs. main hand)
        EntityEquipmentSlot slot = EntityEquipmentSlot.values()[ID / 100];
        ItemStack item = player.getItemStackFromSlot(slot);

        if (ID % 100 == DMLConstants.GuiIDs.DEEP_LEARNER && item.getItem() instanceof ItemDeepLearner) {
            return new ContainerDeepLearner(player.inventory, world, slot, item);
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // Find triggering slot (offhand vs. main hand)
        EntityEquipmentSlot slot = EntityEquipmentSlot.values()[ID / 100];
        ItemStack item = player.getItemStackFromSlot(slot);

        if (item.getItem().getToolClasses(item).contains("wrench")) {
            return null;
        }

        if (ID % 100 == DMLConstants.GuiIDs.DEEP_LEARNER && item.getItem() instanceof ItemDeepLearner) {
            return new DeepLearnerGui(player.inventory, world, slot, item);
        }

        return null;
    }
}
