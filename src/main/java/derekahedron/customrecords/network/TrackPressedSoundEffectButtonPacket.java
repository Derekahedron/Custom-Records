package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.UpdatePressedSoundEffectButtonPacketHandler;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record TrackPressedSoundEffectButtonPacket(UUID playerId, List<SlotReference> slotReferences) {

    public TrackPressedSoundEffectButtonPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readUUID(),
                buffer.readList(SlotReference::fromNetwork));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerId);
        buffer.writeCollection(slotReferences, SlotReference::toNetwork);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        UpdatePressedSoundEffectButtonPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
