package mustapelto.deepmoblearning.common.network;

import io.netty.buffer.ByteBuf;
import mustapelto.deepmoblearning.DMLConfig;
import mustapelto.deepmoblearning.common.items.ItemLivingMatter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageLivingMatterConsume implements IMessage {
    private boolean consumeStack;

    public MessageLivingMatterConsume() {
        consumeStack = false;
    }

    public MessageLivingMatterConsume(boolean consumeStack) {
        this.consumeStack = consumeStack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        consumeStack = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(consumeStack);
    }

    public static class Handler implements IMessageHandler<MessageLivingMatterConsume, IMessage> {
        @Override
        public IMessage onMessage(MessageLivingMatterConsume message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();

            if (mainHand.getItem() instanceof ItemLivingMatter) {
                player.getServerWorld().addScheduledTask(() -> consumeMatter(mainHand, message.consumeStack, player));
            } else if (offHand.getItem() instanceof ItemLivingMatter) {
                player.getServerWorld().addScheduledTask(() -> consumeMatter(offHand, message.consumeStack, player));
            }

            return null;
        }

        private void consumeMatter(ItemStack matterStack, boolean consumeStack, EntityPlayerMP player) {
            ItemLivingMatter matterItem = (ItemLivingMatter) matterStack.getItem();
            int xp = DMLConfig.LIVING_MATTER.getLivingMatterXP(matterItem.getType());

            if (consumeStack) {
                int size = matterStack.getCount();
                matterStack.shrink(size);
                player.addExperience(xp * size);
            } else {
                matterStack.shrink(1);
                player.addExperience(xp);
            }
        }
    }
}
