package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.JukeboxPlaybackPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record RemoveTrackedJukeboxPlaybacksPacket(UUID playerId) {

    public RemoveTrackedJukeboxPlaybacksPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.playerId);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        JukeboxPlaybackPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
