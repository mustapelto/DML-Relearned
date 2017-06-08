package mustapelto.deepmoblearning.common.items;

import mustapelto.deepmoblearning.DMLConstants;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDeepLearner extends ItemBase {
    public ItemDeepLearner() {
        super("deep_learner", 1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        Item mainHand = playerIn.getHeldItemMainhand().getItem();
        Item offHand = playerIn.getHeldItemOffhand().getItem();

        if (!worldIn.isRemote && (mainHand instanceof ItemDeepLearner || (offHand instanceof ItemDeepLearner && mainHand instanceof ItemAir))) {
            playerIn.openGui(DMLRelearned.instance, DMLConstants.GuiIDs.DEEP_LEARNER, worldIn, 0, 0, 0);
        }

        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static NonNullList<ItemStack> getContainedItems(ItemStack deepLearner) {
        NonNullList<ItemStack> items = NonNullList.withSize(DMLConstants.DeepLearner.INTERNAL_SLOTS, ItemStack.EMPTY);

        if (deepLearner.hasTagCompound()) {
            NBTTagList inventory = deepLearner.getTagCompound().getTagList("inventory", Constants.NBT.TAG_COMPOUND);
        }
    }
}
