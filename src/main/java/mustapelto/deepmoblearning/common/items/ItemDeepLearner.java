package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.common.DMLGuiHandler;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import mustapelto.deepmoblearning.common.util.KeyboardHelper;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDeepLearner extends DMLItem implements IGuiItem {
    public ItemDeepLearner() {
        super("deep_learner", 1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        Item mainHand = playerIn.getHeldItemMainhand().getItem();
        Item offHand = playerIn.getHeldItemOffhand().getItem();

        if (!worldIn.isRemote && (mainHand instanceof ItemDeepLearner || (offHand instanceof ItemDeepLearner && mainHand instanceof ItemAir))) {
            DMLGuiHandler.openItemGui(playerIn, handIn == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND);
        }

        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("deepmoblearning.deep_learner.hud"));

        NonNullList<ItemStack> containedDataModels = DataModelHelper.getDataModelStacksFromList(getContainedItems(stack));
        if (containedDataModels.size() > 0) {
            if (!KeyboardHelper.isHoldingSneakKey()) {
                tooltip.add(I18n.format("deepmoblearning.general.more_info", KeyboardHelper.getSneakDisplayName()));
            } else {
                tooltip.add(I18n.format("deepmoblearning.deep_learner.contains"));
                containedDataModels.forEach(dataModel ->
                        tooltip.add(DataModelHelper.getTierDisplayNameFormatted(dataModel) + " " + dataModel.getDisplayName()));
            }
        }
    }

    public static NonNullList<ItemStack> getContainedItems(ItemStack deepLearner) {
        NonNullList<ItemStack> items = NonNullList.withSize(DMLConstants.DeepLearner.INTERNAL_SLOTS, ItemStack.EMPTY);
        NBTTagList inventory = NBTHelper.getCompoundList(deepLearner, "inventory");

        if (inventory != null) {
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
    public int getGuiID() {
        return DMLConstants.GuiIDs.DEEP_LEARNER;
    }
}
