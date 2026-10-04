package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.JukeboxPlaybackPacketHandler;
import derekahedron.customrecords.util.JukeboxPlayback;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record PlayJukeboxTrackPacket(
        UUID playerId,
        SlotReference slotReference,
        JukeboxPlayback playback) {

    public PlayJukeboxTrackPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readUUID(),
                SlotReference.fromNetwork(buffer),
                JukeboxPlayback.fromNetwork(buffer));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerId);
        SlotReference.toNetwork(buffer, slotReference);
        JukeboxPlayback.toNetwork(buffer, playback);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        JukeboxPlaybackPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
