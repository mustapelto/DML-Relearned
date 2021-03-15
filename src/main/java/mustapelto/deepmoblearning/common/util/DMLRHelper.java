package mustapelto.deepmoblearning.common.util;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class DMLRHelper {
    public static boolean isModLoaded(String modID) {
        return modID.equals(DMLConstants.MINECRAFT) || Loader.isModLoaded(modID);
    }

    public static boolean isRegisteredEntity(String entity) {
        return EntityList.isRegistered(new ResourceLocation(entity));
    }

    public static String getRegistryString(String modID, String entryID) {
        return modID + ":" + entryID;
    }

    public static boolean isValidRegistryString(String registryName) {
        return registryName.contains(":");
    }
}
