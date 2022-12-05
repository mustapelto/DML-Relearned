package mustapelto.deepmoblearning.common.items;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import mustapelto.deepmoblearning.client.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.DMLConfig;
import mustapelto.deepmoblearning.common.inventory.ContainerDeepLearner;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDeepLearner extends ItemBase {
    private int inventorySlot = -999;

    public ItemDeepLearner() {
        super("deep_learner", 1);
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            // set inventory slot of this Deep Learner (used to prevent moving Deep Learner while container is open)
            if (handIn == EnumHand.MAIN_HAND)
                inventorySlot = playerIn.inventory.currentItem;
            else if (handIn == EnumHand.OFF_HAND && playerIn.getHeldItemMainhand().getItem() instanceof ItemAir)
                inventorySlot = -1; // -1 == offhand
            else
                inventorySlot = -999; // probably offhand activation with non-empty main hand -> don't open GUI

            if (inventorySlot != -999)
                playerIn.openGui(DMLRelearned.instance, DMLConstants.Gui.IDs.DEEP_LEARNER, worldIn, 0, 0, 0);
        }

        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("deepmoblearning.deep_learner.hud"));

        ImmutableList<ItemStack> containedDataModels = DataModelHelper.getDataModelStacksFromList(getContainedItems(stack));
        if (containedDataModels.size() > 0) {
            if (!KeyboardHelper.isHoldingSneakKey()) {
                tooltip.add(I18n.format("deepmoblearning.general.more_info", KeyboardHelper.getSneakKeyName()));
            } else {
                tooltip.add(I18n.format("deepmoblearning.deep_learner.contains"));
                containedDataModels.forEach(dataModel -> {
                    if (DMLConfig.MISC_SETTINGS.SHOW_TIER_IN_NAME)
                        tooltip.add(dataModel.getDisplayName());
                    else
                        tooltip.add(DataModelHelper.getTierDisplayNameFormatted(dataModel) + " " + dataModel.getDisplayName());
                });
            }
        }
    }

    public static NonNullList<ItemStack> getContainedItems(ItemStack deepLearner) {
        NonNullList<ItemStack> items = NonNullList.withSize(ContainerDeepLearner.INTERNAL_SLOTS, ItemStack.EMPTY);
        NBTTagList inventory = NBTHelper.getTagList(deepLearner, "inventory");

        if (!inventory.isEmpty()) {
            for (int i = 0; i < inventory.tagCount(); i++) {
                NBTTagCompound tagCompound = inventory.getCompoundTagAt(i);
                items.set(i, new ItemStack(tagCompound));
            }
        }

        return items;
    }

    public static void setContainedItems(ItemStack deepLearner, NonNullList<ItemStack> items) {
        NBTTagList inventory = new NBTTagList();

        for (ItemStack stack : items) {
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            inventory.appendTag(tag);
        }

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("inventory", inventory);
        deepLearner.setTagCompound(compound);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.AQUA + super.getItemStackDisplayName(stack) + TextFormatting.RESET;
    }
}
