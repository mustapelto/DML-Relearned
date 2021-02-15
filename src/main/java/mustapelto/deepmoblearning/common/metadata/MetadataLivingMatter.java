package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.DMLRegistry;
import mustapelto.deepmoblearning.common.util.JsonHelper;
import mustapelto.deepmoblearning.common.util.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

/**
 * Created by mustapelto on 2021-02-14
 */
public class MetadataLivingMatter extends Metadata {
    public static final MetadataLivingMatter INVALID = new MetadataLivingMatter();

    // Data from JSON
    private final String displayName; // Name shown in tooltips and GUI. Also used as item display name. Default: metadataID
    private final TextFormatting displayColor; // Color of name when displayed. Default: white
    private final int xpValue; // XP received when single item is consumed. Default: 10
    private ImmutableList<String> craftingRecipeStrings; // Recipes using this item in JSON string format

    // Calculated data
    private final ResourceLocation livingMatterRegistryName;
    private final String displayNameFormatted;
    private ItemStack itemStack;

    private MetadataLivingMatter() {
        super("", "");

        displayName = "INVALID";
        displayColor = TextFormatting.WHITE;
        xpValue = 0;
        craftingRecipeStrings = ImmutableList.of();
        livingMatterRegistryName = new ResourceLocation("");
        displayNameFormatted = "INVALID";
    }

    public MetadataLivingMatter(JsonObject data, String categoryID, String metadataID) {
        super(categoryID, metadataID);

        displayName = JsonHelper.getString(data, "displayName", StringHelper.uppercaseFirst(metadataID));
        String displayColorString = JsonHelper.getString(data, "displayColor", "white");
        TextFormatting displayFormatting = TextFormatting.getValueByName(displayColorString);
        displayColor = (displayFormatting != null) ? displayFormatting : TextFormatting.WHITE;
        xpValue = JsonHelper.getInt(data, "xpValue", 10, 0, 1000);
        craftingRecipeStrings = JsonHelper.getStringListFromJsonArray(data, "craftingRecipes");

        livingMatterRegistryName = new ResourceLocation(DMLConstants.ModInfo.ID, "living_matter_" + metadataID);
        displayNameFormatted = StringHelper.getFormattedString(displayName, displayColor);
    }

    @Override
    public void finalizeData() {
        itemStack = DMLRegistry.getLivingMatter(getMetadataID());
    }

    @Override
    public boolean isInvalid() {
        return this.equals(INVALID);
    }

    public String getDisplayNameFormatted() {
        return displayNameFormatted;
    }

    public int getXpValue() {
        return xpValue;
    }

    public ItemStack getItemStack(int size) {
        ItemStack result = itemStack.copy();
        result.setCount(size);
        return result;
    }

    public ItemStack getItemStack() {
        return getItemStack(1);
    }

    public ResourceLocation getLivingMatterRegistryName() {
        return livingMatterRegistryName;
    }

    public ResourceLocation getLivingMatterTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + livingMatterRegistryName.getResourcePath() + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + livingMatterRegistryName.getResourcePath());
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Living Matter texture for {} not found. Using default texture.", getMetadataID());
            return DMLConstants.DefaultModels.LIVING_MATTER;
        }
    }
}
