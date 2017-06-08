package mustapelto.deepmoblearning;

import mustapelto.deepmoblearning.common.DMLGuiHandler;
import mustapelto.deepmoblearning.common.ServerProxy;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.registry.ItemRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = DMLConstants.ModInfo.ID, name = DMLConstants.ModInfo.NAME, version = DMLConstants.ModInfo.VERSION,
    dependencies = DMLConstants.ModDependencies.DEP_STRING)
public class DMLRelearned
{
    @Mod.Instance(DMLConstants.ModInfo.ID)
    public static DMLRelearned instance;

    public static Logger logger = LogManager.getLogger(DMLConstants.ModInfo.ID);

    @SidedProxy(
            clientSide = "mustapelto.deepmoblearning.client.ClientProxy",
            serverSide = "mustapelto.deepmoblearning.common.ServerProxy"
    )
    public static ServerProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        DMLPacketHandler.registerPackets();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new DMLGuiHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    public static CreativeTabs creativeTab = new CreativeTabs(DMLConstants.ModInfo.ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ItemRegistry.living_matter_overworldian);
        }
    };
}

