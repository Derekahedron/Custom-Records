package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.UpdatePressedSoundEffectButtonPacketHandler;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record UpdatePressedSoundEffectButtonPacket(UUID playerId, SlotReference slotReference, boolean isPressed) {

    public UpdatePressedSoundEffectButtonPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readUUID(),
                SlotReference.fromNetwork(buffer),
                buffer.readBoolean());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerId);
        SlotReference.toNetwork(buffer, slotReference);
        buffer.writeBoolean(isPressed);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        UpdatePressedSoundEffectButtonPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
