package derekahedron.customrecords.network;

import com.mojang.datafixers.util.Either;
import derekahedron.customrecords.client.network.PlaySoundEffectButtonPacketHandler;
import derekahedron.customrecords.util.Tuple;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record StartSoundEffectPacket(
        Either<Tuple<ResourceKey<Level>, BlockPos>, Tuple<UUID, SlotReference>> location,
        SoundEvent soundEffect,
        long randomSeed,
        boolean isGlobal) {

    public StartSoundEffectPacket(
            ResourceKey<Level> dimension,
            BlockPos blockPos,
            SoundEvent soundEffect,
            long randomSeed,
            boolean isGlobal) {
        this(
                Either.left(new Tuple<>(dimension, blockPos)),
                soundEffect,
                randomSeed,
                isGlobal);
    }

    public StartSoundEffectPacket(
            UUID playerId,
            SlotReference slotReference,
            SoundEvent soundEffect,
            long randomSeed,
            boolean isGlobal) {
        this(
                Either.right(new Tuple<>(playerId, slotReference)),
                soundEffect,
                randomSeed,
                isGlobal);
    }

    public StartSoundEffectPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readEither(
                        Tuple.reader(
                                b -> b.readResourceKey(Registries.DIMENSION),
                                FriendlyByteBuf::readBlockPos),
                        Tuple.reader(
                                FriendlyByteBuf::readUUID,
                                SlotReference::fromNetwork)),
                SoundEvent.createVariableRangeEvent(buffer.readResourceLocation()),
                buffer.readLong(),
                buffer.readBoolean());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeEither(
                location,
                Tuple.writer(
                        FriendlyByteBuf::writeResourceKey,
                        FriendlyByteBuf::writeBlockPos),
                Tuple.writer(
                        FriendlyByteBuf::writeUUID,
                        SlotReference::toNetwork));
        buffer.writeResourceLocation(soundEffect.getLocation());
        buffer.writeLong(randomSeed);
        buffer.writeBoolean(isGlobal);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        PlaySoundEffectButtonPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
