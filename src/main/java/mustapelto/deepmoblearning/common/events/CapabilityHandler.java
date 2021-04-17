package mustapelto.deepmoblearning.common.events;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.client.gui.GuiTrialOverlay;
import mustapelto.deepmoblearning.common.capabilities.CapabilityPlayerTrialProvider;
import mustapelto.deepmoblearning.common.capabilities.ICapabilityPlayerTrial;
import mustapelto.deepmoblearning.common.tiles.TileEntityTrialKeystone;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class CapabilityHandler {
    private static final ResourceLocation PLAYER_TRIAL = new ResourceLocation(DMLConstants.ModInfo.ID, "player_trial");

    @SubscribeEvent
    public static void attachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(PLAYER_TRIAL, new CapabilityPlayerTrialProvider());
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void playerClone(PlayerEvent.Clone event) {
        EntityPlayer newPlayer = event.getEntityPlayer();
        EntityPlayer oldPlayer = event.getOriginal();
        World newPlayerWorld = newPlayer.getEntityWorld();

        if (!newPlayerWorld.isRemote) {
            ICapabilityPlayerTrial newCap = newPlayer.getCapability(CapabilityPlayerTrialProvider.PLAYER_TRIAL_CAPABILITY, null);
            ICapabilityPlayerTrial oldCap = oldPlayer.getCapability(CapabilityPlayerTrialProvider.PLAYER_TRIAL_CAPABILITY, null);

            if (newCap == null || oldCap == null)
                return;

            if (!(newPlayerWorld.getTileEntity(oldCap.getTrialKeystonePos()) instanceof TileEntityTrialKeystone))
                // Trial Keystone was broken or otherwise removed
                return;

            newCap.copy(oldCap);

            if (newPlayer instanceof EntityPlayerMP)
                newCap.syncToClient((EntityPlayerMP) newPlayer);
        } else {
            GuiTrialOverlay.INSTANCE.setPlayerCapability();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void playerJoinedWorldClient(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerSP)
            GuiTrialOverlay.INSTANCE.setPlayerCapability();
    }
}
