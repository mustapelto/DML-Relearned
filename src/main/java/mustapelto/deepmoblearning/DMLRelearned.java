package mustapelto.deepmoblearning;

import mustapelto.deepmoblearning.common.DMLGuiHandler;
import mustapelto.deepmoblearning.common.DMLRegistry;
import mustapelto.deepmoblearning.common.ServerProxy;
import mustapelto.deepmoblearning.common.metadata.DataModelTierDataManager;
import mustapelto.deepmoblearning.common.metadata.LivingMatterDataManager;
import mustapelto.deepmoblearning.common.metadata.MobMetaDataManager;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.util.FileHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = DMLConstants.ModInfo.ID, name = DMLConstants.ModInfo.NAME, version = DMLConstants.ModInfo.VERSION,
    dependencies = DMLConstants.ModDependencies.DEP_STRING)
public class DMLRelearned
{
    @Mod.Instance(DMLConstants.ModInfo.ID)
    public static DMLRelearned instance;

    public static Logger logger;

    @SidedProxy(
            clientSide = "mustapelto.deepmoblearning.client.ClientProxy",
            serverSide = "mustapelto.deepmoblearning.common.ServerProxy"
    )
    public static ServerProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        FileHelper.init(event);

        // Init Data Managers (for reading/writing config json files)
        // LivingMatterData has to be initialized before MobMetaData because MobMetaData needs a default LivingMatter value
        LivingMatterDataManager.init();
        MobMetaDataManager.init();
        DataModelTierDataManager.init();

        // Network Stuff
        DMLPacketHandler.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new DMLGuiHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerGuiRenderers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    public static final CreativeTabs creativeTab = new CreativeTabs(DMLConstants.ModInfo.ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(DMLRegistry.itemDeepLearner);
        }
    };
}

