package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.items.ItemCreativeModelLearner;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.DataModelHelper.CreativeLevelUpAction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;

public class MessageLevelUpModel implements IMessage {
    private CreativeLevelUpAction increaseTier; // false -> add one kill; true -> add one tier

    public MessageLevelUpModel() {}

    public MessageLevelUpModel(CreativeLevelUpAction increaseTier) {
        this.increaseTier = increaseTier;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        increaseTier = CreativeLevelUpAction.fromInt(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(increaseTier.toInt());
    }

    public static class Handler implements IMessageHandler<MessageLevelUpModel, IMessage> {
        @Override
        public IMessage onMessage(MessageLevelUpModel message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            Item mainHand = player.getHeldItemMainhand().getItem();
            Item offHand = player.getHeldItemOffhand().getItem();

            if (mainHand instanceof ItemCreativeModelLearner || offHand instanceof ItemCreativeModelLearner) {
                player.getServerWorld().addScheduledTask(() -> {
                    DataModelHelper.findAndLevelUpModels(player.inventory.mainInventory, player, message.increaseTier);
                    DataModelHelper.findAndLevelUpModels(player.inventory.offHandInventory, player, message.increaseTier);
                });
            }

            return null;
        }
    }
}
