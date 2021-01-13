package mustapelto.deepmoblearning.common.mobdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.common.enums.EnumLivingMatterType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* TODO:
   - "minecraft" mod ID as default
   - "displayEntityID" defaults to itemID:modID
   - "associatedMobs" defaults to itemID:modID
 */

public class MobMetaData {
    private final String itemID;
    private final String modID;
    private final String displayName;
    private final String displayNamePlural;
    private final int numberOfHearts;
    private final String livingMatter;
    private final String[] mobTrivia;
    private final int simulationRFCost;
    private final String extraTooltip;
    private final String displayEntityID;
    private final String displayEntityHeldItem;
    private final int displayEntityScale;
    private final int displayEntityOffsetX;
    private final int displayEntityOffsetY;
    private final String displayExtraEntityID;
    private final boolean displayExtraEntityIsChild;
    private final int displayExtraEntityOffsetX;
    private final int displayExtraEntityOffsetY;
    private final String[] associatedMobs;
    private final String[] lootItems;
    private final String[] trialRewards;

    public MobMetaData(JsonObject data) {
        // Check if all required fields are present
        String missingItems = "";
        if (!data.has("itemID"))
            missingItems += "itemID";
        if (!data.has("modID"))
            missingItems += (missingItems.length() > 0 ? ", " : "") + "modID";
        if (!data.has("simulationRFCost"))
            missingItems += (missingItems.length() > 0 ? ", " : "") + "simulationRFCost";
        if (!data.has("associatedMobs"))
            missingItems += (missingItems.length() > 0 ? ", " : "") + "associatedMobs";
        if (!data.has("lootItems"))
            missingItems += (missingItems.length() > 0 ? ", " : "") + "lootItems";

        if (missingItems.length() > 0) {
            DMLRelearned.logger.error(missingItems);
            throw new IllegalArgumentException(missingItems);
        }

        itemID = data.get("itemID").getAsString();
        modID = data.get("modID").getAsString();
        displayName = getOrDefault(data, "displayName", "");
        displayNamePlural = getOrDefault(data, "displayNamePlural", "");
        numberOfHearts = getOrDefault(data, "numberOfHearts", 0);
        livingMatter = getOrDefault(data, "livingMatter", EnumLivingMatterType.OVERWORLDIAN.getName());
        mobTrivia = getOrDefault(data, "mobTrivia", new String[0]);
        simulationRFCost = data.get("simulationRFCost").getAsInt();
        extraTooltip = getOrDefault(data, "extraTooltip", "");
        displayEntityID = getOrDefault(data, "displayEntityID", "");
        displayEntityHeldItem = getOrDefault(data, "displayEntityHeldItem", "");
        displayEntityScale = getOrDefault(data, "displayEntityScale", 1);
        displayEntityOffsetX = getOrDefault(data, "displayEntityOffsetX", 0);
        displayEntityOffsetY = getOrDefault(data, "displayEntityOffsetY", 0);
        displayExtraEntityID = getOrDefault(data, "displayExtraEntityID", "");
        displayExtraEntityIsChild = getOrDefault(data, "displayExtraEntityIsChild", false);
        displayExtraEntityOffsetX = getOrDefault(data, "displayExtraEntityOffsetX", 0);
        displayExtraEntityOffsetY = getOrDefault(data, "displayExtraEntityOffsetY", 0);
        associatedMobs = fromArray(data.get("associatedMobs").getAsJsonArray());
        lootItems = fromArray(data.get("lootItems").getAsJsonArray());
        trialRewards = getOrDefault(data, "trialRewards", new String[0]);
    }

    // Field Getters and Helpers

    public String getItemID() {
        return itemID;
    }

    public boolean isModLoaded() {
        return modID.equals(DMLConstants.MINECRAFT) || Loader.isModLoaded(modID);
    }

    public String getDisplayName() {
        return !displayName.isEmpty() ? displayName : I18n.format("deepmoblearning.mob_data.unknown_name");
    }

    public String getDisplayNamePlural() {
        return !displayNamePlural.isEmpty() ? displayNamePlural : getDisplayName() + "s";
    }

    public int getNumberOfHearts() {
        return numberOfHearts;
    }

    public String getLivingMatter() {
        return livingMatter;
    }

    public String[] getMobTrivia() {
        return mobTrivia.length > 0 ? mobTrivia : new String[]{I18n.format("deepmoblearning.mob_data.unknown_trivia")};
    }

    public int getSimulationRFCost() {
        return simulationRFCost;
    }

    public String getExtraTooltip() {
        return extraTooltip;
    }

    public Entity getEntity(World world) {
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(displayEntityID), world);
        if (!displayEntityHeldItem.isEmpty() && entity instanceof EntityLiving) {
            Item heldItem = Item.getByNameOrId(displayEntityHeldItem);
            if (heldItem != null)
                ((EntityLiving) entity).setHeldItem(EnumHand.MAIN_HAND, new ItemStack(heldItem));
        }

        return entity;
    }

    public int getDisplayEntityScale() {
        return displayEntityScale;
    }

    public int getDisplayEntityOffsetX() {
        return displayEntityOffsetX;
    }

    public int getDisplayEntityOffsetY() {
        return displayEntityOffsetY;
    }

    public Entity getExtraEntity(World world) {
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(displayExtraEntityID), world);
        if (displayExtraEntityIsChild) {
            if (entity instanceof EntityZombie) {
                ((EntityZombie) entity).setChild(true);
            } else if (entity instanceof EntityAgeable) {
                ((EntityAgeable) entity).setGrowingAge(-1000000); // big number -> won't grow up while Deep Learner is open
            }
        }
        return entity;
    }

    public int getDisplayExtraEntityOffsetX() {
        return displayExtraEntityOffsetX;
    }

    public int getDisplayExtraEntityOffsetY() {
        return displayExtraEntityOffsetY;
    }

    public String[] getAssociatedMobs() {
        return associatedMobs;
    }

    public String[] getLootItems() {
        return lootItems;
    }

    public String[] getTrialRewards() {
        return trialRewards;
    }

    public boolean isAssociatedMob(EntityLivingBase entity) {
        EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
        if (entityEntry == null)
            return false;

        ResourceLocation registryName = entityEntry.getRegistryName();
        if (registryName == null)
            return false;

        String name = registryName.toString();
        return Arrays.asList(associatedMobs).contains(name);
    }

    // Methods used for instance initialization / reading from JSON

    private String getOrDefault(JsonObject data, String key, String defaultValue) {
        return data.has(key) ? data.get(key).getAsString() : defaultValue;
    }

    private int getOrDefault(JsonObject data, String key, int defaultValue) {
        return data.has(key) ? data.get(key).getAsInt() : defaultValue;
    }

    private boolean getOrDefault(JsonObject data, String key, boolean defaultValue) {
        return data.has(key) ? data.get(key).getAsBoolean() : defaultValue;
    }

    private String[] getOrDefault(JsonObject data, String key, String[] defaultValue) {
        return data.has(key) ? fromArray(data.get(key).getAsJsonArray()) : defaultValue;
    }

    private String[] fromArray(JsonArray input) {
        String[] result = new String[input.size()];

        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).getAsString();
        }

        return result;
    }

    // Create JSON object with supplied data

    public static JsonObject createJsonObject(String itemID, String modID, String displayName, String displayNamePlural,
                               int numberOfHearts, String livingMatter, String[] mobTrivia,
                               int simulationRFCost, String extraTooltip, String displayEntityID,
                               String displayEntityHeldItem, int displayEntityScale, int displayEntityOffsetX,
                               int displayEntityOffsetY, String displayExtraEntityID,
                               boolean displayExtraEntityIsChild, int displayExtraEntityOffsetX,
                               int displayExtraEntityOffsetY, String[] associatedMobs,
                               String[] lootItems, String[] trialRewards) {
        JsonObject object = new JsonObject();

        object.addProperty("itemID", itemID);
        object.addProperty("modID", modID);
        object.addProperty("displayName", displayName);
        if (!displayNamePlural.equals(""))
            object.addProperty("displayNamePlural", displayNamePlural);
        object.addProperty("numberOfHearts", numberOfHearts);
        object.addProperty("livingMatter", livingMatter);
        object.add("mobTrivia", toJsonArray(mobTrivia));
        object.addProperty("simulationRFCost", simulationRFCost);
        if (!extraTooltip.equals(""))
            object.addProperty("extraTooltip", extraTooltip);
        object.addProperty("displayEntityID", displayEntityID);
        if (!displayEntityHeldItem.equals(""))
            object.addProperty("displayEntityHeldItem", displayEntityHeldItem);
        object.addProperty("displayEntityScale", displayEntityScale);
        object.addProperty("displayEntityOffsetX", displayEntityOffsetX);
        object.addProperty("displayEntityOffsetY", displayEntityOffsetY);
        if (!displayExtraEntityID.equals("")) {
            object.addProperty("displayExtraEntityID", displayExtraEntityID);
            object.addProperty("displayExtraEntityIsChild", displayExtraEntityIsChild);
            object.addProperty("displayExtraEntityOffsetX", displayExtraEntityOffsetX);
            object.addProperty("displayExtraEntityOffsetY", displayExtraEntityOffsetY);
        }
        object.add("associatedMobs", toJsonArray(associatedMobs));
        object.add("lootItems", toJsonArray(lootItems));
        if (trialRewards.length > 0)
            object.add("trialRewards", toJsonArray(trialRewards));

        return object;
    }

    private static JsonArray toJsonArray(String[] list) {
        JsonArray result = new JsonArray();
        for (String item : list) {
            result.add(item);
        }
        return result;
    }
}