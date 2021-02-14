package mustapelto.deepmoblearning.common.util;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mustapelto on 2021-02-13
 * Partially derived from brandonscore.lib.StackReference by brandon3055.
 * Stores information about an ItemStack read from a string input.
 */
public class ItemStringBuilder {
    private final String registryName;
    private final boolean isOre;
    private final int metadata;
    private final int stackSize;
    private final NBTTagCompound nbt;

    private ItemStringBuilder(String registryName, int stackSize, int metadata, NBTTagCompound nbt) {
        this.registryName = registryName;
        isOre = registryName.startsWith("ore:");
        this.stackSize = stackSize;
        this.metadata = metadata;
        this.nbt = nbt;
    }

    /**
     * Factory method, extracts item stack information from a string.
     * Format: {@code "item[,amount[,metadata[,nbt]]]}
     * i.e. same as the /give command, except comma separated.
     *
     * Everything except the item name is optional, but each entry requires
     * those before it to be present (it is not possible to e.g. omit the
     * amount but have metadata).
     *
     * Item can be in registry name format (e.g. "minecraft:stone"), in
     * Forge Ore format (e.g. "ore:ingotCopper"), or the string "this",
     * which means "the item this recipe is attached to" (used in Living Matter recipes).
     *
     * Default values:
     * Amount = 1
     * Metadata = 0
     * NBT = null
     *
     * NBT must be in JSON string format.
     *
     * @param itemString The string which contains the item data.
     * @param thisName Name to replace the string "this" with
     */
    public static ItemStringBuilder fromString(String itemString, String thisName) {
        if (itemString.isEmpty()) {
            DMLRelearned.logger.warn("ItemStackBuilder: Input string empty");
            return null;
        }

        // Split input string into separate strings for each value (item, amount, metadata, nbt)
        // NB: this also splits the NBT string if it contains a comma, so it has to be rejoined later
        final String separator = ",";
        final String[] values = itemString.split(separator);

        String registryName = values[0];
        if (registryName.equals("this")) {
            if (thisName.isEmpty()) {
                DMLRelearned.logger.warn("ItemStackBuilder: \"this\" not allowed in this context");
                return null;
            }
            registryName = thisName;
        }

        if (!registryName.contains(":")) {
            DMLRelearned.logger.warn("ItemStackBuilder: Invalid item name: {}", registryName);
            return null;
        }

        // Read stack size
        int stackSize = 1;
        if (values.length > 1) {
            try {
                stackSize = Integer.parseInt(values[1]);
            } catch (NumberFormatException e) {
                DMLRelearned.logger.warn("ItemStackBuilder: Invalid stack size entry: {}", values[1]);
                DMLRelearned.logger.warn("Error message: {}", e.getMessage());
            }
        }

        // Read metadata
        int metadata = 0;
        if (values.length > 2) {
            try {
                metadata = Integer.parseInt(values[2]);
            } catch (NumberFormatException e) {
                DMLRelearned.logger.warn("ItemStackBuilder: Invalid metadata entry: {}", values[2]);
                DMLRelearned.logger.warn("Error message: {}", e.getMessage());
            }
        }

        // Read NBT
        NBTTagCompound nbt = null;
        if (values.length > 3) {
            // Rejoin the possibly split NBT string
            String nbtString = String.join(separator, Arrays.copyOfRange(values, 3, values.length));
            try {
                nbt = JsonToNBT.getTagFromJson(nbtString);
            } catch (NBTException e) {
                DMLRelearned.logger.warn("ItemStackBuilder: Invalid NBT string: {}", nbtString);
                DMLRelearned.logger.warn("Error message: {}", e.getMessage());
            }
        }

        return new ItemStringBuilder(registryName, stackSize, metadata, nbt);
    }

    public ItemStack getItemStack() {
        ItemStack result;

        if (isOre) {
            String oreName = registryName.substring(4);
            if (!OreDictionary.doesOreNameExist(oreName)) {
                DMLRelearned.logger.warn("ItemStackBuilder: Ore with name \"{}\" does not exist", oreName);
                return ItemStack.EMPTY;
            }
            result = OreDictionary.getOres(oreName).get(0).copy(); // Set to first available item with this name
            result.setCount(stackSize);
            result.setItemDamage(metadata);
        } else {
            Item item = Item.getByNameOrId(registryName);
            Block block = Block.getBlockFromName(registryName);
            if (item == null && (block == null || block == Blocks.AIR))
                return ItemStack.EMPTY;

            if (item != null)
                result = new ItemStack(item, stackSize, metadata);
            else
                result = new ItemStack(block, stackSize, metadata);
        }

        result.setTagCompound(nbt);
        return result;
    }

    public Ingredient getIngredient() {
        if (isOre) {
            String oreName = registryName.substring(4);
            if (!OreDictionary.doesOreNameExist(oreName)) {
                DMLRelearned.logger.warn("ItemStackBuilder: Ore with name \"{}\" does not exist", oreName);
                return Ingredient.EMPTY;
            }
            return new OreIngredient(oreName);
        }

        ItemStack stack = getItemStack();
        if (stack.isEmpty())
            return Ingredient.EMPTY;

        return Ingredient.fromStacks(stack);
    }

    public static ImmutableList<ItemStack> itemStackListFromStringList(List<String> inputList, String thisName) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

        for (String entry : inputList) {
            ItemStringBuilder itemStringBuilder = ItemStringBuilder.fromString(entry, thisName);
            if (itemStringBuilder == null)
                continue;

            ItemStack entryStack = itemStringBuilder.getItemStack();
            if (!entryStack.isEmpty())
                builder.add(entryStack);
        }

        return builder.build();
    }

    public static ImmutableList<ItemStack> itemStackListFromStringList(List<String> inputList) {
        return itemStackListFromStringList(inputList, "");
    }

    public static ImmutableList<Ingredient> ingredientListFromStringList(List<String> inputList, String thisName) {
        ImmutableList.Builder<Ingredient> builder = ImmutableList.builder();

        for (String entry : inputList) {
            ItemStringBuilder itemStringBuilder = ItemStringBuilder.fromString(entry, thisName);
            if (itemStringBuilder == null)
                continue;

            builder.add(itemStringBuilder.getIngredient());
        }

        return builder.build();
    }

    public static ImmutableList<Ingredient> ingredientListFromStringList(List<String> inputList) {
        return ingredientListFromStringList(inputList, "");
    }
}
