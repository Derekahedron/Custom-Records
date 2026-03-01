package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.PlaySophisticatedCustomRecordPacketHandler;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record PlaySophisticatedCustomRecordPacket(
        UUID storageUuid,
        Item item,
        ResourceLocation trackId,
        Either<BlockPos, Integer> source) {

    public PlaySophisticatedCustomRecordPacket(UUID storageUuid, Item item, ResourceLocation trackId, BlockPos pos) {
        this(storageUuid, item, trackId, Either.left(pos));
    }

    public PlaySophisticatedCustomRecordPacket(UUID storageUuid, Item item, ResourceLocation trackId, int entityId) {
        this(storageUuid, item, trackId, Either.right(entityId));
    }

    public PlaySophisticatedCustomRecordPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readUUID(),
                Item.byId(buffer.readInt()),
                buffer.readResourceLocation(),
                buffer.readEither(FriendlyByteBuf::readBlockPos, FriendlyByteBuf::readInt));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(storageUuid);
        buffer.writeInt(Item.getId(item));
        buffer.writeResourceLocation(trackId);
        buffer.writeEither(source, FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::writeInt);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        PlaySophisticatedCustomRecordPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
