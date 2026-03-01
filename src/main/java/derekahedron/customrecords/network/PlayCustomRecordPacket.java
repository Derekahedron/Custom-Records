package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.PlayCustomRecordPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayCustomRecordPacket(BlockPos blockPos, Item item, ResourceLocation trackId) {

    public PlayCustomRecordPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readBlockPos(),
                Item.byId(buffer.readInt()),
                buffer.readResourceLocation());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeInt(Item.getId(item));
        buffer.writeResourceLocation(trackId);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        PlayCustomRecordPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
