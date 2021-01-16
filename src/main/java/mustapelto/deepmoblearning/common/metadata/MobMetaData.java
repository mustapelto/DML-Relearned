package mustapelto.deepmoblearning.common.metadata;

import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Arrays;

public class MobMetaData extends MetaDataBase {
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
        String[] requiredFields = new String[]{
                "itemID"
        };

        validate(data, requiredFields, "MobData");

        itemID = getOrDefault(data, "itemID", "");
        modID = getOrDefault(data, "modID", "minecraft");
        displayName = getOrDefault(data, "displayName", "");
        displayNamePlural = getOrDefault(data, "displayNamePlural", "");
        numberOfHearts = getOrDefault(data, "numberOfHearts", 0);
        livingMatter = getOrDefault(data, "livingMatter", DMLConstants.LivingMatter.DEFAULT_VALUES.OVERWORLDIAN.ID);
        mobTrivia = getOrDefault(data, "mobTrivia", new String[0]);
        simulationRFCost = getOrDefault(data, "simulationRFCost", 256);
        extraTooltip = getOrDefault(data, "extraTooltip", "");
        displayEntityID = getOrDefault(data, "displayEntityID", String.format("%s:%s", modID, itemID));
        displayEntityHeldItem = getOrDefault(data, "displayEntityHeldItem", "");
        displayEntityScale = getOrDefault(data, "displayEntityScale", 40);
        displayEntityOffsetX = getOrDefault(data, "displayEntityOffsetX", 0);
        displayEntityOffsetY = getOrDefault(data, "displayEntityOffsetY", 0);
        displayExtraEntityID = getOrDefault(data, "displayExtraEntityID", "");
        displayExtraEntityIsChild = getOrDefault(data, "displayExtraEntityIsChild", false);
        displayExtraEntityOffsetX = getOrDefault(data, "displayExtraEntityOffsetX", 0);
        displayExtraEntityOffsetY = getOrDefault(data, "displayExtraEntityOffsetY", 0);
        associatedMobs = getOrDefault(data, "associatedMobs", new String[]{ displayEntityID });
        lootItems = getOrDefault(data, "lootItems", new String[]{ "minecraft:wood" });
        trialRewards = getOrDefault(data, "trialRewards", new String[0]);
    }

    // Field Getters and Helpers

    public String getDisplayName() {
        return !displayName.isEmpty() ? displayName : I18n.format("deepmoblearning.mob_data.unknown_name");
    }

    public String getDisplayNamePlural() {
        return !displayNamePlural.isEmpty() ? displayNamePlural : getDisplayName() + "s";
    }

    public int getNumberOfHearts() {
        return numberOfHearts;
    }

    public LivingMatterData getLivingMatterData() {
        return LivingMatterDataManager.getByID(livingMatter);
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

    /**
     * Create JSON object from supplied data
     * @param itemID ID for associated item (internal name)
     * @param modID ID of associated mod (vanilla = "minecraft")
     * @param displayName Entity name to display (Deep Learner, model name) (default = "Unknown")
     * @param displayNamePlural Plural form of entity name (default =  displayName + "s")
     * @param numberOfHearts Entity's max health (default = 40)
     * @param livingMatter Associated living matter type (must be in living matter list) (default = "overworldian")
     * @param mobTrivia Trivia text for Deep Learner (default = "Nothing is known about this mob.")
     * @param simulationRFCost Cost for model simulation (RF/t) (default = 256)
     * @param extraTooltip Extra tooltip shown on hover (default = none)
     * @param displayEntityID ID of entity to be displayed in Deep Learner (default = modID:itemID)
     * @param displayEntityHeldItem Item held by displayed entity (default = none)
     * @param displayEntityScale Scale of displayed entity (default = 10)
     * @param displayEntityOffsetX GUI x axis offset of displayed entity (default = 0)
     * @param displayEntityOffsetY GUI y axis offset of displayed entity (default = 0)
     * @param displayExtraEntityID ID of extra entity to be displayed in Deep Learner (e.g. Zombie child, Cave Spider) (default = none)
     * @param displayExtraEntityIsChild Is extra entity a child? (Only works for zombies and animals)
     * @param displayExtraEntityOffsetX GUI x axis offset of extra entity (default = 0)
     * @param displayExtraEntityOffsetY GUI y axis offset of extra entity (default = 0)
     * @param associatedMobs Mobs that can be killed to level up model
     * @param lootItems Items that can be made from pristine matter
     * @param trialRewards Rewards for max level trial win (only for mobs that have a trial)
     * @return A JSON object with all relevant data.
     */
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
        if (!displayName.equals(DMLConstants.MINECRAFT))
            object.addProperty("modID", modID);
        object.addProperty("displayName", displayName);
        if (!displayNamePlural.equals(""))
            object.addProperty("displayNamePlural", displayNamePlural);
        object.addProperty("numberOfHearts", numberOfHearts);
        object.addProperty("livingMatter", livingMatter);
        object.add("mobTrivia", stringArrayToJsonArray(mobTrivia));
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
        object.add("associatedMobs", stringArrayToJsonArray(associatedMobs));
        object.add("lootItems", stringArrayToJsonArray(lootItems));
        if (trialRewards.length > 0)
            object.add("trialRewards", stringArrayToJsonArray(trialRewards));

        return object;
    }
}