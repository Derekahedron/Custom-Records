package derekahedron.customrecords.client.network;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.client.sound.StartSoundEffectEvent;
import derekahedron.customrecords.client.util.ClientActiveSoundsManager;
import derekahedron.customrecords.network.StartSoundEffectPacket;
import derekahedron.customrecords.network.StopSoundEffectPacket;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class PlaySoundEffectButtonPacketHandler {

    public static void handlePacket(StartSoundEffectPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        StartSoundEffectEvent event = new StartSoundEffectEvent(
                player,
                packet.soundEffect(),
                packet.location().map(
                        tuple -> SoundSource.BLOCKS,
                        tuple -> SoundSource.PLAYERS),
                packet.randomSeed(),
                packet.isGlobal());
        if (CustomRecords.EVENT_BUS.post(event)) return;

        SoundEvent soundEffect = event.getSoundEffect();
        SoundSource soundSource = event.getSoundSource();
        long randomSeed = event.getRandomSeed();

        packet.location()
                .ifLeft(tuple -> {
                    ResourceKey<Level> dimension = tuple.a();
                    BlockPos blockPos = tuple.b();

                    if (packet.isGlobal()) {
                        ClientActiveSoundsManager.playSoundInstance(
                                dimension,
                                blockPos,
                                new SimpleSoundInstance(
                                        soundEffect.getLocation(),
                                        soundSource,
                                        1.0F,
                                        1.0F,
                                        RandomSource.create(randomSeed),
                                        false,
                                        0,
                                        SoundInstance.Attenuation.NONE,
                                        0.0D,
                                        0.0D,
                                        0.0D,
                                        true));
                    } else {
                        ClientActiveSoundsManager.playSoundInstance(
                                dimension,
                                blockPos,
                                new SimpleSoundInstance(
                                        soundEffect,
                                        soundSource,
                                        1.0F,
                                        1.0F,
                                        RandomSource.create(randomSeed),
                                        blockPos.getX() + 0.5D,
                                        blockPos.getY() + 0.5D,
                                        blockPos.getZ() + 0.5D));
                    }
                })
                .ifRight(tuple -> {
                    UUID playerId = tuple.a();
                    SlotReference slotReference = tuple.b();

                    if (packet.isGlobal() || playerId.equals(player.getUUID())) {
                        ClientActiveSoundsManager.playSoundInstance(
                                playerId,
                                slotReference,
                                new SimpleSoundInstance(
                                        soundEffect.getLocation(),
                                        soundSource,
                                        1.0F,
                                        1.0F,
                                        RandomSource.create(randomSeed),
                                        false,
                                        0,
                                        SoundInstance.Attenuation.NONE,
                                        0.0D,
                                        0.0D,
                                        0.0D,
                                        true));
                    } else {
                        Entity entity = player.level().getPlayerByUUID(playerId);
                        if (entity == null) {
                            ClientActiveSoundsManager.playSoundInstance(playerId, slotReference, null);
                            return;
                        }

                        ClientActiveSoundsManager.playSoundInstance(
                                playerId,
                                slotReference,
                                new EntityBoundSoundInstance(
                                        soundEffect,
                                        soundSource,
                                        1.0F,
                                        1.0F,
                                        entity,
                                        randomSeed));
                    }
                });
    }

    public static void handlePacket(StopSoundEffectPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        packet.location()
                .ifLeft(tuple -> {
                    ResourceKey<Level> dimension = tuple.a();
                    BlockPos blockPos = tuple.b();

                    ClientActiveSoundsManager.playSoundInstance(
                            dimension,
                            blockPos,
                            null);
                })
                .ifRight(tuple -> {
                    UUID playerId = tuple.a();
                    SlotReference slotReference = tuple.b();

                    ClientActiveSoundsManager.playSoundInstance(
                            playerId,
                            slotReference,
                            null);
                });
    }
}
