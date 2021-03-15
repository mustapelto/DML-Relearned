package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.DMLRegistry;
import mustapelto.deepmoblearning.common.util.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class MetadataLivingMatter extends Metadata {
    // JSON Keys
    private static final String LIVING_MATTER_ID = "id";
    private static final String MOD_ID = "mod";
    private static final String DISPLAY_NAME = "displayName";
    private static final String DISPLAY_COLOR = "displayColor";
    private static final String XP_VALUE = "xpValue";

    // Validation
    private static final String[] REQUIRED_KEYS = new String[] {
            LIVING_MATTER_ID
    };

    // Default Values
    private static final String DEFAULT_LIVING_MATTER_ID = "";
    private static final String DEFAULT_MOD_ID = "minecraft";
    private static final String DEFAULT_DISPLAY_COLOR = "white";
    private static final int DEFAULT_XP_VALUE = 10;

    // Data from JSON
    private final String livingMatterID;
    private final String modID;
    private final String displayName; // Name shown in tooltips and GUI. Also used as item display name. Default: metadataID
    private final int xpValue; // XP received when single item is consumed. Default: 10

    // Calculated data
    private final String displayNameFormatted;
    private ItemStack itemStack;

    public MetadataLivingMatter(JsonObject data) throws IllegalArgumentException {
        if (isInvalidJson(data, REQUIRED_KEYS)) {
            throw new IllegalArgumentException("Invalid Data Model JSON entry!");
        }

        livingMatterID = getString(data, LIVING_MATTER_ID)
                .orElse(DEFAULT_LIVING_MATTER_ID);
        modID = getString(data, MOD_ID)
                .orElse(DEFAULT_MOD_ID);

        displayName = getString(data, DISPLAY_NAME)
                .orElse(StringHelper.uppercaseFirst(livingMatterID));
        String displayColorString = getString(data, DISPLAY_COLOR)
                .orElse(DEFAULT_DISPLAY_COLOR);
        TextFormatting displayColor = StringHelper.getValidFormatting(displayColorString);
        displayNameFormatted = StringHelper.getFormattedString(displayName, displayColor);

        xpValue = getInt(data, XP_VALUE, 0, 1000)
                .orElse(DEFAULT_XP_VALUE);
    }

    @Override
    public void finalizeData() {
        itemStack = DMLRegistry.getLivingMatter(livingMatterID);
    }

    @Override
    public String getID() {
        return livingMatterID;
    }

    public String getModID() {
        return modID;
    }

    public String getRegistryID() {
        return "living_matter_" + livingMatterID;
    }

    public String getDisplayName() {
        return displayName;
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

    public ResourceLocation getLivingMatterTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + getRegistryID() + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + getRegistryID());
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Living Matter texture not found for entry: {}. Using default texture.", livingMatterID);
            return DMLConstants.DefaultModels.LIVING_MATTER;
        }
    }
}
