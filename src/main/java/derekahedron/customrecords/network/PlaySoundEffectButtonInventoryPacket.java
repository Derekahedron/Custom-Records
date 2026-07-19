package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.PlaySoundEffectButtonPacketHandler;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public record PlaySoundEffectButtonInventoryPacket(
        UUID playerId,
        SlotReference slotReference,
        Optional<SoundEvent> soundEffect,
        boolean isGlobal) {

    public PlaySoundEffectButtonInventoryPacket(
            UUID playerId,
            SlotReference slotReference,
            SoundEvent soundEffect,
            boolean isGlobal) {
        this(
                playerId,
                slotReference,
                Optional.of(soundEffect),
                isGlobal);
    }

    public PlaySoundEffectButtonInventoryPacket(
            UUID playerId,
            SlotReference slotReference,
            boolean isGlobal) {
        this(
                playerId,
                slotReference,
                Optional.empty(),
                isGlobal);
    }

    public PlaySoundEffectButtonInventoryPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readUUID(),
                SlotReference.fromNetwork(buffer),
                buffer.readOptional(
                        (b) -> SoundEvent.createVariableRangeEvent(b.readResourceLocation())),
                buffer.readBoolean());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerId);
        SlotReference.toNetwork(buffer, slotReference);
        buffer.writeOptional(soundEffect,
                (b, soundEvent) -> b.writeResourceLocation(soundEvent.getLocation()));
        buffer.writeBoolean(isGlobal);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        PlaySoundEffectButtonPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
