package mustapelto.deepmoblearning.common.metadata;

import mustapelto.deepmoblearning.DMLConstants;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by mustapelto on 2021-02-14
 */
public abstract class Metadata {
    protected final String categoryID;
    protected final String metadataID;

    public Metadata(String categoryID, String metadataID) {
        this.categoryID = categoryID;
        this.metadataID = metadataID;
    }

    public abstract void finalizeData();

    public String getMetadataID() {
        return metadataID;
    }

    public boolean isModLoaded() {
        return categoryID.equals(DMLConstants.MINECRAFT) || Loader.isModLoaded(categoryID);
    }
}
