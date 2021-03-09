package mustapelto.deepmoblearning.common.commands;

import mustapelto.deepmoblearning.common.items.ItemLegacyBase;
import mustapelto.deepmoblearning.common.tiles.TileEntityBase;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class CommandConvert extends CommandBase {
    @Override
    @Nonnull
    public String getName() {
        return "convert";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "deepmoblearning.commands.dml.convert.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<TileEntity> tileEntities = sender.getEntityWorld().loadedTileEntityList;

        tileEntities.stream()
                .filter(te -> !(te instanceof TileEntityBase) && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
                .forEach(te -> {
                   IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                   if (itemHandler == null)
                       return;
                   for (int i = 0; i < itemHandler.getSlots(); i++) {
                       ItemStack stack = itemHandler.getStackInSlot(i);
                       Item item = stack.getItem();
                       if (item instanceof ItemLegacyBase) {
                           ItemStack converted = ((ItemLegacyBase) item).getConvertedItemStack(stack);
                           itemHandler.extractItem(i, stack.getCount(), false);
                           itemHandler.insertItem(i, con)
                       }
                   }
                });
    }
}
