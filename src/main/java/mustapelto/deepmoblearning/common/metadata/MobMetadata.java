package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.models.ModelDataModel;
import mustapelto.deepmoblearning.client.models.ModelPristineMatter;
import mustapelto.deepmoblearning.common.DMLRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;

import static mustapelto.deepmoblearning.common.util.JsonHelper.getOrDefault;
import static mustapelto.deepmoblearning.common.util.JsonHelper.stringArrayToJsonArray;

public class MobMetadata {
    private final String modID; // Mod ID the item is related to. "minecraft" for vanilla-related items.
    private final String itemID; // Used for item registry and texture loading. No default value, item won't load if itemID is not set
    private final String displayName; // Used on Data Model and Pristine Matter items and in Deep Learner GUI. Default: itemID with first letter capitalized
    private final String displayNamePlural; // Plural form of display name. Used in Deep Learner GUI. Default: displayName + "s"
    private final LivingMatterData livingMatter; // Associated Living Matter type. Default: first available
    private final int simulationRFCost; // Cost to simulate this mob in RF/t. Default: 256
    private final String extraTooltip; // Extra tooltip to display on Data Model item. Default: ""
    private final String[] associatedMobs; // List of mobs that will increase model data. Format: "modid:mobname" (e.g. "minecraft:blaze"). Default: modID:itemID
    private final String[] lootItems; // List of available loot items (produced from Pristine Matter in Loot Fabricator). Format: "modid:itemid" (e.g. "minecraft:stone"). Default: "minecraft:wood"
    private final String[] trialRewards; // List of rewards from completed trial at max tier. Default: empty list
    private final int numberOfHearts; // Number of hearts to show in Deep Learner GUI. Default: 10
    private final String[] mobTrivia; // Trivia text to show in Deep Learner GUI. Default: "Nothing is known about this mob."
    private final String displayEntityID; // ID of entity to display in Deep Learner GUI. Default: modID:itemID
    private final String displayEntityHeldItem; // Item held by entity in Deep Learner GUI. Default: ""
    private final int displayEntityScale; // Scale of entity in Deep Learner GUI. Default: 40
    private final int displayEntityOffsetX; // X offset of entity in Deep Learner GUI. Default: 0
    private final int displayEntityOffsetY; // Y offset of entity in Deep Learner GUI. Default: 0
    private final String displayExtraEntityID; // ID of additional entity to display in Deep Learner GUI. Default: ""
    private final boolean displayExtraEntityIsChild; // Is additional entity a child form (e.g. zombie)? Default: false
    private final int displayExtraEntityOffsetX; // X offset of additional entity in Deep Learner GUI. Default: 0
    private final int displayExtraEntityOffsetY; // Y offset of additional entity in Deep Learner GUI. Default: 0

    private final String modelName; // Name of data model item = "data_model_" + itemID
    private final String pristineName; // Name of pristine matter item = "pristine_matter_" + itemID

    private final ImmutableList<ItemStack> lootItemList; // List of configured loot items for this mob (built once at init)
    private final ImmutableList<ItemStack> trialRewardItemList; // List of configured trial reward items for this mob (built once at init)

    private MobMetadata(String modID, JsonObject data) {
        if (!data.has("itemID")) {
            throw new IllegalArgumentException("Item ID missing on Data Model entry. Cannot create items.");
        }

        itemID = data.get("itemID").getAsString();
        modelName = "data_model_" + itemID; // Name of associated data model item
        pristineName = "pristine_matter_" + itemID; // Name of associated pristine matter item

        this.modID = modID;

        displayName = getOrDefault(data, "displayName", itemID.substring(0, 1).toUpperCase() + itemID.substring(1));
        displayNamePlural = getOrDefault(data, "displayNamePlural", "");

        String livingMatterString = getOrDefault(data, "livingMatter", "");
        livingMatter = LivingMatterDataManager.getByID(livingMatterString);

        simulationRFCost = getOrDefault(data, "simulationRFCost", 256, 0, DMLConstants.SimulationChamber.ENERGY_IN_MAX);
        extraTooltip = getOrDefault(data, "extraTooltip", "");

        String defaultEntityString = String.format("%s:%s", modID, itemID); // Used if entry for associatedMobs or deepLearnerDisplay.entityID is empty

        associatedMobs = getOrDefault(data, "associatedMobs", new String[]{ defaultEntityString });
        lootItems = getOrDefault(data, "lootItems", new String[]{ "minecraft:wood" });
        trialRewards = getOrDefault(data, "trialRewards", new String[0]);

        // Read Deep Learner Display Settings
        JsonObject displaySettings = data.get("deepLearnerDisplay").getAsJsonObject();

        numberOfHearts = getOrDefault(displaySettings, "numberOfHearts", 0, 0, Integer.MAX_VALUE);
        mobTrivia = getOrDefault(displaySettings, "mobTrivia", new String[]{ "Nothing is known about this mob." });
        displayEntityID = getOrDefault(displaySettings, "entityID", String.format("%s:%s", modID, itemID));
        displayEntityHeldItem = getOrDefault(displaySettings, "entityHeldItem", "");
        displayEntityScale = getOrDefault(displaySettings, "entityScale", 40, 0, 200);
        displayEntityOffsetX = getOrDefault(displaySettings, "entityOffsetX", 0, -200, 200);
        displayEntityOffsetY = getOrDefault(displaySettings, "entityOffsetY", 0, -200, 200);
        displayExtraEntityID = getOrDefault(displaySettings, "extraEntityID", "");
        displayExtraEntityIsChild = getOrDefault(displaySettings, "extraEntityIsChild", false);
        displayExtraEntityOffsetX = getOrDefault(displaySettings, "extraEntityOffsetX", 0, -200, 200);
        displayExtraEntityOffsetY = getOrDefault(displaySettings, "extraEntityOffsetY", 0, -200, 200);

        lootItemList = buildItemListFromStringArray(lootItems);
        trialRewardItemList = buildItemListFromStringArray(trialRewards);
    }

    public static MobMetadata deserialize(String modID, JsonObject data) {
        try {
            return new MobMetadata(modID, data);
        } catch (IllegalArgumentException e) {
            DMLRelearned.logger.warn(e.getMessage());
            return null;
        }
    }

    // Field Getters and Helpers
    public boolean isModLoaded() {
        return modID.equals(DMLConstants.MINECRAFT) || Loader.isModLoaded(modID);
    }

    public String getModID() {
        return modID;
    }

    public String getItemID() {
        return itemID;
    }

    public ResourceLocation getDataModelTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + modelName + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + modelName);
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Data Model texture for {} not found. Using default texture.", itemID);
            return ModelDataModel.DEFAULT_LOCATION;
        }
    }

    public ResourceLocation getPristineMatterTexture() {
        try {
            // Will throw FileNotFoundException if texture file doesn't exist in mod jar or resource packs
            ResourceLocation locationFromId = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/items/" + pristineName + ".png");
            Minecraft.getMinecraft().getResourceManager().getAllResources(locationFromId);
            return new ResourceLocation(DMLConstants.ModInfo.ID, "items/" + pristineName);
        } catch (IOException e) {
            // File not found -> use default model and output info
            DMLRelearned.logger.info("Pristine Matter texture for {} not found. Using default texture.", itemID);
            return ModelPristineMatter.DEFAULT_LOCATION;
        }
    }

    @Nonnull
    public LivingMatterData getLivingMatterData() {
        return livingMatter;
    }

    @Nonnull
    public ItemStack getPristineMatter() {
        return getPristineMatter(1);
    }

    @Nonnull
    public ItemStack getPristineMatter(int size) {
        return new ItemStack(DMLRegistry.registeredPristineMatter.get(itemID), size);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNamePlural() {
        return !displayNamePlural.isEmpty() ? displayNamePlural : getDisplayName() + "s";
    }

    public int getNumberOfHearts() {
        return numberOfHearts;
    }

    public String[] getMobTrivia() {
        return mobTrivia;
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

    public ImmutableList<ItemStack> getLootItemList() {
        return lootItemList;
    }

    public ItemStack getLootItem(int index) {
        if (index >= 0 && index < lootItemList.size())
            return lootItemList.get(index).copy();

        return ItemStack.EMPTY;
    }

    public ImmutableList<ItemStack> getTrialRewardItemList() {
        return trialRewardItemList;
    }

    public ItemStack getTrialRewardItem(int index) {
        if (index >= 0 && index < trialRewardItemList.size())
            return trialRewardItemList.get(index).copy();

        return ItemStack.EMPTY;
    }

    private static ImmutableList<ItemStack> buildItemListFromStringArray(String[] inputList) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

        for (String entry : inputList) {
            ItemStack entryStack = getStackFromString(entry);
            if (!entryStack.isEmpty()) {
                builder.add(entryStack);
            }
        }

        return builder.build();
    }

    /**
     * Produce ItemStack from string
     *
     * Input format: {@code "modid:itemid,n[,m][,{nbt}]} where
     * - n: item amount
     * - m: metadata/damage value (optional)
     * - nbt: nbt data (optional)
     *
     * @param line String array entry representing item
     * @return An ItemStack according to the string input
     */
    private static ItemStack getStackFromString(String line) {
        String[] values = line.split(",");

        if (values.length < 2) // Invalid entry
            return ItemStack.EMPTY;

        String itemName = values[0];
        int amount;
        int meta = 0;
        NBTTagCompound nbt = null;

        try {
            amount = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            DMLRelearned.logger.error("Invalid item entry (amount not a valid number");
            return ItemStack.EMPTY;
        }

        if (values.length > 2) {
            boolean couldReadMeta;
            try {
                meta = Integer.parseInt(values[2]);
                couldReadMeta = true;
            } catch (NumberFormatException e) {
                couldReadMeta = false;
            }

            if (!couldReadMeta) {
                nbt = getNBT(buildNBTString(values, 2));
            }
            else if (values.length > 3) {
                nbt = getNBT(buildNBTString(values, 3));
            }
        }

        Item item = Item.getByNameOrId(itemName);
        if (item == null)
            return ItemStack.EMPTY;

        ItemStack resultStack = new ItemStack(item, amount, meta);

        if (nbt != null)
            resultStack.setTagCompound(nbt);

        return resultStack;
    }

    private static String buildNBTString(String[] values, int startIndex) {
        StringBuilder nbtString = new StringBuilder();
        for (int i = startIndex; i < values.length; i++) {
            nbtString.append(values[i]);
            if (i < values.length - 1)
                nbtString.append(",");
        }
        return nbtString.toString();
    }

    private static NBTTagCompound getNBT(String nbtString) {
        try {
            return JsonToNBT.getTagFromJson(nbtString);
        } catch (NBTException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("itemID", itemID);
        object.addProperty("displayName", displayName);
        if (!displayNamePlural.equals(""))
            object.addProperty("displayNamePlural", displayNamePlural);
        object.addProperty("livingMatter", livingMatter.getItemID());
        object.addProperty("simulationRFCost", simulationRFCost);
        if (!extraTooltip.equals(""))
            object.addProperty("extraTooltip", extraTooltip);
        object.add("associatedMobs", stringArrayToJsonArray(associatedMobs));
        object.add("lootItems", stringArrayToJsonArray(lootItems));
        if (trialRewards.length > 0)
            object.add("trialRewards", stringArrayToJsonArray(trialRewards));

        JsonObject deepLearnerDisplay = new JsonObject();

        deepLearnerDisplay.addProperty("numberOfHearts", numberOfHearts);
        deepLearnerDisplay.add("mobTrivia", stringArrayToJsonArray(mobTrivia));
        deepLearnerDisplay.addProperty("entityID", displayEntityID);
        if (!displayEntityHeldItem.equals(""))
            deepLearnerDisplay.addProperty("entityHeldItem", displayEntityHeldItem);
        deepLearnerDisplay.addProperty("entityScale", displayEntityScale);
        deepLearnerDisplay.addProperty("entityOffsetX", displayEntityOffsetX);
        deepLearnerDisplay.addProperty("entityOffsetY", displayEntityOffsetY);
        if (!displayExtraEntityID.equals("")) {
            deepLearnerDisplay.addProperty("extraEntityID", displayExtraEntityID);
            deepLearnerDisplay.addProperty("extraEntityIsChild", displayExtraEntityIsChild);
            deepLearnerDisplay.addProperty("extraEntityOffsetX", displayExtraEntityOffsetX);
            deepLearnerDisplay.addProperty("extraEntityOffsetY", displayExtraEntityOffsetY);
        }

        object.add("deepLearnerDisplay", deepLearnerDisplay);

        return object;
    }
}