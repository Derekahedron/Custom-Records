package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.PlaySoundEffectButtonPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public record PlaySoundEffectButtonPacket(BlockPos blockPos, Optional<SoundEvent> soundEffect) {

    public PlaySoundEffectButtonPacket(BlockPos blockPos, SoundEvent soundEffect) {
        this(blockPos, Optional.of(soundEffect));
    }

    public PlaySoundEffectButtonPacket(BlockPos blockPos) {
        this(blockPos, Optional.empty());
    }

    public PlaySoundEffectButtonPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readBlockPos(),
                buffer.readOptional((b) -> SoundEvent.createVariableRangeEvent(b.readResourceLocation())));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeOptional(soundEffect, (b, soundEvent) -> b.writeResourceLocation(soundEvent.getLocation()));
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        PlaySoundEffectButtonPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
