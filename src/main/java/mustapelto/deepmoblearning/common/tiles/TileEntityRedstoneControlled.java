package mustapelto.deepmoblearning.common.tiles;

import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageRedstoneModeToServer;
import mustapelto.deepmoblearning.common.network.MessageRedstonePowerToClient;
import mustapelto.deepmoblearning.common.util.NBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityRedstoneControlled extends TileEntity {
    protected boolean redstonePowered;
    protected int redstoneLevel;
    protected RedstoneMode redstoneMode = RedstoneMode.ALWAYS_ON;

    @Nonnull
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public boolean isRedstonePowered() {
        return redstonePowered;
    }

    public void setRedstonePowered(boolean redstonePowered) {
        this.redstonePowered = redstonePowered;
        if (world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    public void setRedstoneMode(RedstoneMode mode) {
        redstoneMode = mode;
        if (world.isRemote) {
            DMLPacketHandler.sendToServer(new MessageRedstoneModeToServer(this));
        }
    }

    public boolean isRedstoneActive() {
        return RedstoneMode.isActive(redstoneMode, redstonePowered);
    }

    public void onBlockPlaced() {
        onNeighborChange();
    }

    public void onNeighborChange() {
        boolean oldRedstonePowerState = redstonePowered;
        redstoneLevel = world.isBlockIndirectlyGettingPowered(pos);
        redstonePowered = redstoneLevel > 0;

        if (redstonePowered != oldRedstonePowerState) {
            DMLPacketHandler.sendToClient(new MessageRedstonePowerToClient(this), world, pos);
        }
    }

    //
    // Chunk / Block load
    //

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
        if (pkt.getPos() != pos)
            return;

        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    //
    // NBT write/read
    //

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("redstoneLevel", redstoneLevel);
        compound.setBoolean("redstonePowered", redstonePowered);
        compound.setInteger("redstoneMode", redstoneMode.getIndex());

        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        redstoneLevel = NBTHelper.getInteger(compound, "redstoneLevel", 0);
        redstonePowered = NBTHelper.getBoolean(compound, "redstonePowered", false);
        redstoneMode = RedstoneMode.byIndex(NBTHelper.getInteger(compound, "redstoneMode", 0));
    }
}
