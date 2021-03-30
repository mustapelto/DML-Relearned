package mustapelto.deepmoblearning.client.renderers;

import mustapelto.deepmoblearning.common.tiles.TileEntityTrialKeystone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

public class TESRTrialKeystone extends TileEntitySpecialRenderer<TileEntityTrialKeystone> {
    private static final double KEY_OFFSET_Y = 1.0;
    private static final double KEY_SCALE = 0.5;
    private static final int KEY_BOBBING_TIME = 16;
    private static final double KEY_BOBBING_AMPLITUDE = 1/16d;
    private static final int KEY_ROTATION_SPEED = 4;

    @Override
    public void render(TileEntityTrialKeystone te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = te.getTrialKey();
        if (stack.isEmpty())
            return;

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();

        float partialTick = te.getWorld().getTotalWorldTime() + partialTicks;
        double offset = Math.sin(partialTick / KEY_BOBBING_TIME) * KEY_BOBBING_AMPLITUDE;
        float angle = partialTick * KEY_ROTATION_SPEED;

        GlStateManager.translate(x + 0.5, y + KEY_OFFSET_Y + offset, z + 0.5);
        GlStateManager.rotate(angle, 0, 1, 0);
        GlStateManager.scale(KEY_SCALE, KEY_SCALE, KEY_SCALE);

        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
}
