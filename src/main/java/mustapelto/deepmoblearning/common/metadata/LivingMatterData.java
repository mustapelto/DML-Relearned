package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelLivingMatter;
import mustapelto.deepmoblearning.common.DMLRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;

import java.io.IOException;

import static mustapelto.deepmoblearning.common.util.JsonHelper.getOrDefault;

public class LivingMatterData {
    private final String modID;
    private final String itemID;
    private final String displayName; // Name shown in tooltips and GUI. Also used for item display name.
    private final String displayColor; // Color of name when displayed.
    private final int xpValue; // XP received when item is consumed.

    private final String itemName;

    private LivingMatterData(String modID, JsonObject data) {
        if (!data.has("itemID")) {
            throw new IllegalArgumentException("Item ID missing on Data Model entry. Cannot create items.");
        }

        itemID = data.get("itemID").getAsString();
        this.modID = modID;
        itemName = "living_matter_" + itemID;

        displayName = getOrDefault(data, "displayName", itemID.substring(0, 1).toUpperCase() + itemID.substring(1));
        displayColor = getOrDefault(data, "displayColor", "white");
        xpValue = getOrDefault(data, "xpValue", 0, 0, Integer.MAX_VALUE);
    }

    public static LivingMatterData create(String modID, JsonObject data) {
        try {
            return new LivingMatterData(modID, data);
        } catch (IllegalArgumentException e) {
            DMLRelearned.logger.warn(e.getMessage());
            return null;
        }
    }

    public ResourceLocation getLivingMatterTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + itemName + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + itemName);
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Living Matter texture for {} not found. Using default texture.", itemID);
            return ModelLivingMatter.DEFAULT_LOCATION;
        }
    }

    @Nonnull
    public ItemStack getItemStack(int size) {
        return new ItemStack(DMLRegistry.registeredLivingMatter.get(itemID), size);
    }

    @Nonnull
    public ItemStack getItemStack() {
        return getItemStack(1);
    }

    public String getModID() {
        return modID;
    }

    public String getItemID() {
        return itemID;
    }

    public boolean isModLoaded() {
        return modID.equals(DMLConstants.MINECRAFT) || Loader.isModLoaded(modID);
    }

    public String getDisplayNameFormatted() {
        TextFormatting formatting = TextFormatting.getValueByName(displayColor);
        return (formatting != null) ?
                formatting + displayName + TextFormatting.RESET :
                displayName;
    }

    public int getXpValue() {
        return xpValue;
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty("itemID", itemID);
        object.addProperty("displayName", displayName);
        object.addProperty("displayColor", displayColor);
        object.addProperty("xpValue", xpValue);

        return object;
    }
}
