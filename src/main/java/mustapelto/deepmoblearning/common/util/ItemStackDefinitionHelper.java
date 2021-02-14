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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mustapelto on 2021-02-13
 * Partially derived from brandonscore.lib.StackReference by brandon3055.
 * Stores information about an ItemStack read from a string input.
 */
public class ItemStackDefinitionHelper {
    public static ItemStack itemStackFromString(String itemDefinitionString) {
        ItemStackDefinition itemStackDefinition = itemDefinitionFromString(itemDefinitionString);
        if (itemStackDefinition.isInvalid())
            return ItemStack.EMPTY;

        ItemStack result;

        if (itemStackDefinition.isOre) {
            String oreName = itemStackDefinition.registryName.substring(4);
            if (!OreDictionary.doesOreNameExist(oreName)) {
                DMLRelearned.logger.warn("ItemStackBuilder: Ore with name \"{}\" does not exist", oreName);
                return ItemStack.EMPTY;
            }
            result = OreDictionary.getOres(oreName).get(0).copy(); // Set to first available item with this name
            result.setCount(itemStackDefinition.stackSize);
            result.setItemDamage(itemStackDefinition.metadata);
        } else {
            Item item = Item.getByNameOrId(itemStackDefinition.registryName);
            Block block = Block.getBlockFromName(itemStackDefinition.registryName);
            if (item == null && (block == null || block == Blocks.AIR))
                return ItemStack.EMPTY;

            if (item != null)
                result = new ItemStack(item, itemStackDefinition.stackSize, itemStackDefinition.metadata);
            else
                result = new ItemStack(block, itemStackDefinition.stackSize, itemStackDefinition.metadata);
        }

        result.setTagCompound(itemStackDefinition.nbt);
        return result;
    }

    public static ImmutableList<ItemStack> itemStackListFromStringList(List<String> inputList) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

        for (String entry : inputList) {
            ItemStack entryStack = itemStackFromString(entry);
            if (!entryStack.isEmpty())
                builder.add(entryStack);
        }

        return builder.build();
    }

    public static Ingredient ingredientFromString(String itemDefinitionString) {
        ItemStackDefinition itemStackDefinition = itemDefinitionFromString(itemDefinitionString);
        if (itemStackDefinition.isInvalid())
            return Ingredient.EMPTY;

        if (itemStackDefinition.isOre) {
            String oreName = itemStackDefinition.registryName.substring(4);
            if (!OreDictionary.doesOreNameExist(oreName)) {
                DMLRelearned.logger.warn("ItemStackBuilder: Ore with name \"{}\" does not exist", oreName);
                return Ingredient.EMPTY;
            }
            return new OreIngredient(oreName);
        }

        ItemStack stack = itemStackFromString(itemDefinitionString);
        if (stack.isEmpty())
            return Ingredient.EMPTY;

        return Ingredient.fromStacks(stack);
    }

    public static ImmutableList<Ingredient> ingredientListFromStringList(List<String> inputList) {
        ImmutableList.Builder<Ingredient> builder = ImmutableList.builder();

        for (String entry : inputList) {
            Ingredient ingredient = ingredientFromString(entry);
            if (!ingredient.equals(Ingredient.EMPTY))
                builder.add(ingredient);
        }

        return builder.build();
    }

    /**
     * Extract ItemStack definition (name, amount, metadata, nbt) from a string.
     * Format: {@code "item[,amount[,metadata[,nbt]]]}
     * i.e. same as the /give command, except comma separated.
     *
     * Everything except the item name is optional, but each entry requires
     * those before it to be present (it is not possible to e.g. omit the
     * amount but have metadata).
     *
     * Item can be in registry name format (e.g. "minecraft:stone"), in
     * Forge Ore format (e.g. "ore:ingotCopper")
     *
     * Default values:
     * Amount = 1
     * Metadata = 0
     * NBT = null
     *
     * NBT must be in JSON string format.
     *
     * @param itemString The string which contains the item data.
     */
    @Nonnull
    private static ItemStackDefinition itemDefinitionFromString(String itemString) {
        if (itemString.isEmpty()) {
            DMLRelearned.logger.warn("ItemStackBuilder: Input string empty");
            return ItemStackDefinition.INVALID;
        }

        // Split input string into separate strings for each value (item, amount, metadata, nbt)
        // NB: this also splits the NBT string if it contains a comma, so it has to be rejoined later
        final String separator = ",";
        final String[] values = itemString.split(separator);

        String registryName = values[0];

        if (!registryName.contains(":")) {
            DMLRelearned.logger.warn("ItemStackBuilder: Invalid item name: {}", registryName);
            return ItemStackDefinition.INVALID;
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

        return new ItemStackDefinition(registryName, stackSize, metadata, nbt);
    }

    private static class ItemStackDefinition {
        public final String registryName;
        public final boolean isOre;
        public final int stackSize;
        public final int metadata;
        public final NBTTagCompound nbt;

        private ItemStackDefinition() {
            registryName = "";
            isOre = false;
            stackSize = 0;
            metadata = 0;
            nbt = null;
        }

        public ItemStackDefinition(String registryName, int stackSize, int metadata, NBTTagCompound nbt) {
            this.registryName = registryName;
            isOre = registryName.startsWith("ore:");
            this.stackSize = stackSize;
            this.metadata = metadata;
            this.nbt = nbt;
        }

        public boolean isInvalid() {
            return this.equals(INVALID);
        }

        public static final ItemStackDefinition INVALID = new ItemStackDefinition();
    }
}
