package mustapelto.deepmoblearning.common.tiles;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mustapelto.deepmoblearning.common.network.DMLPacketHandler;
import mustapelto.deepmoblearning.common.network.MessageRequestUpdateTileEntity;
import mustapelto.deepmoblearning.common.network.MessageUpdateTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityBase extends TileEntity {
    protected void requestUpdatePacketFromServer() {
        DMLPacketHandler.sendToServer(new MessageRequestUpdateTileEntity(this));
    }

    protected void sendUpdatePacketToClient() {
        markDirty();
        DMLPacketHandler.sendToClient(new MessageUpdateTileEntity(this), world, pos);
    }

    public ByteBuf getUpdateData() {
        ByteBuf buf = Unpooled.buffer();

        buf.writeLong(pos.toLong());

        return buf;
    }

    public abstract void handleUpdateData(ByteBuf buf);


    /**
     * @return true if server should send an update packet to client when requested
     */
    public boolean clientNeedsUpdate() {
        return false;
    }

    @Override
    public boolean shouldRefresh(@Nonnull World world, @Nonnull BlockPos pos, IBlockState oldState, IBlockState newState) {
        return (oldState.getBlock() != newState.getBlock());
    }

    //
    // Chunk / Block Load
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
}
