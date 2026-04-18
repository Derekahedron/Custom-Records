package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.PlaySoundEffectButtonPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public record PlayGlobalSoundEffectButtonPacket(ResourceKey<Level> dimension, BlockPos blockPos, Optional<SoundEvent> soundEffect) {

    public PlayGlobalSoundEffectButtonPacket(ResourceKey<Level> dimension, BlockPos blockPos, SoundEvent soundEffect) {
        this(dimension, blockPos, Optional.of(soundEffect));
    }

    public PlayGlobalSoundEffectButtonPacket(ResourceKey<Level> dimension, BlockPos blockPos) {
        this(dimension, blockPos, Optional.empty());
    }

    public PlayGlobalSoundEffectButtonPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readResourceKey(Registries.DIMENSION),
                buffer.readBlockPos(),
                buffer.readOptional((b) -> SoundEvent.createVariableRangeEvent(b.readResourceLocation())));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeResourceKey(dimension);
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
