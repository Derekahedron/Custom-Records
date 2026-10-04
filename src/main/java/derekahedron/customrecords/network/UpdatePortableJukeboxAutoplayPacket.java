package derekahedron.customrecords.network;

import derekahedron.customrecords.item.PortableJukeboxItemHandler;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UpdatePortableJukeboxAutoplayPacket(
        SlotReference slotReference,
        boolean isAutoplay) {

    public UpdatePortableJukeboxAutoplayPacket(FriendlyByteBuf buffer) {
        this(
                SlotReference.fromNetwork(buffer),
                buffer.readBoolean());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        SlotReference.toNetwork(buffer, slotReference);
        buffer.writeBoolean(isAutoplay);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;

            PortableJukeboxItemHandler handler = PortableJukeboxItemHandler.getHandler(player, slotReference).orElse(null);
            if (handler == null) return;

            handler.isAutoplay = isAutoplay;
            handler.saveAutoplay();
        });
        context.get().setPacketHandled(true);
    }
}
