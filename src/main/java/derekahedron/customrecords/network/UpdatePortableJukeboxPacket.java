package derekahedron.customrecords.network;

import derekahedron.customrecords.item.PortableJukeboxItemHandler;
import derekahedron.customrecords.util.JukeboxPlaybackManager;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UpdatePortableJukeboxPacket(
        SlotReference slotReference,
        int selectedSlot,
        int tickCount,
        boolean isPlaying) {

    public UpdatePortableJukeboxPacket(FriendlyByteBuf buffer) {
        this(
                SlotReference.fromNetwork(buffer),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readBoolean());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        SlotReference.toNetwork(buffer, slotReference);
        buffer.writeInt(selectedSlot);
        buffer.writeInt(tickCount);
        buffer.writeBoolean(isPlaying);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;

            PortableJukeboxItemHandler handler = PortableJukeboxItemHandler.getHandler(player, slotReference)
                    .orElse(null);
            if (handler == null) return;
            if (tickCount < 0) return;
            if (!handler.isSlotValid(selectedSlot)) return;

            handler.selectedSlot = selectedSlot;

            if (!isPlaying) {
                handler.tickCount = tickCount;
                JukeboxPlaybackManager.stopTrack(player, slotReference, handler.isGlobal(), false);
                JukeboxPlaybackManager.removePlayback(player, slotReference);
            } else {
                handler.tickCount = 0;
                handler.createNewPlayback(player.level().registryAccess(), tickCount).ifPresent(playback -> {
                    JukeboxPlaybackManager.startTrack(player, slotReference, playback, false);
                    JukeboxPlaybackManager.addPlayback(player, slotReference, playback);
                });
            }
            handler.save();
        });
        context.get().setPacketHandled(true);
    }
}
