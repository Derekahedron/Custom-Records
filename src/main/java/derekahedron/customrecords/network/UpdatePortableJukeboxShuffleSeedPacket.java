package derekahedron.customrecords.network;

import derekahedron.customrecords.item.PortableJukeboxItemHandler;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public record UpdatePortableJukeboxShuffleSeedPacket(
        SlotReference slotReference,
        Optional<Long> shuffleSeed) {

    public UpdatePortableJukeboxShuffleSeedPacket(FriendlyByteBuf buffer) {
        this(
                SlotReference.fromNetwork(buffer),
                buffer.readOptional(FriendlyByteBuf::readLong));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        SlotReference.toNetwork(buffer, slotReference);
        buffer.writeOptional(shuffleSeed, FriendlyByteBuf::writeLong);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;

            PortableJukeboxItemHandler handler = PortableJukeboxItemHandler.getHandler(player, slotReference).orElse(null);
            if (handler == null) return;

            handler.shuffleSeed = shuffleSeed.orElse(null);
            handler.saveShuffleSeed();
        });
        context.get().setPacketHandled(true);
    }
}
