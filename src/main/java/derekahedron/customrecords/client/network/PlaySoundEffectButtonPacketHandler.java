package derekahedron.customrecords.client.network;

import com.google.common.collect.Maps;
import derekahedron.customrecords.network.PlaySoundEffectButtonInventoryPacket;
import derekahedron.customrecords.network.PlaySoundEffectButtonPacket;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class PlaySoundEffectButtonPacketHandler {

    public static final Map<ResourceKey<Level>, HashMap<BlockPos, AbstractSoundInstance>> ACTIVE_BLOCK_SOUND_EFFECTS = Maps.newHashMap();
    public static final Map<UUID, HashMap<SlotReference, AbstractSoundInstance>> ACTIVE_PLAYER_SOUND_EFFECTS = Maps.newHashMap();

    public static void handlePacket(PlaySoundEffectButtonPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (packet.isGlobal()) {
            playSoundInstance(packet.dimension(), packet.blockPos(), packet.soundEffect().map((soundEvent) -> new SimpleSoundInstance(
                    soundEvent.getLocation(),
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    player.level().random,
                    false,
                    0,
                    SoundInstance.Attenuation.NONE,
                    0.0D,
                    0.0D,
                    0.0D,
                    true)).orElse(null));
        } else {
            playSoundInstance(player.level().dimension(), packet.blockPos(), packet.soundEffect().map((soundEvent) -> new SimpleSoundInstance(
                    soundEvent,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    player.level().random,
                    packet.blockPos().getX() + 0.5D,
                    packet.blockPos().getY() + 0.5D,
                    packet.blockPos().getZ() + 0.5D)).orElse(null));
        }
    }

    public static void handlePacket(PlaySoundEffectButtonInventoryPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (packet.isGlobal() || packet.playerId().equals(player.getUUID())) {
            playSoundInstance(packet.playerId(), packet.slotReference(), packet.soundEffect().map((soundEvent) -> new SimpleSoundInstance(
                    soundEvent.getLocation(),
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    player.level().random,
                    false,
                    0,
                    SoundInstance.Attenuation.NONE,
                    0.0D,
                    0.0D,
                    0.0D,
                    true)).orElse(null));
        } else {
            Entity entity = player.level().getPlayerByUUID(packet.playerId());
            if (entity == null) return;

            playSoundInstance(packet.playerId(), packet.slotReference(), packet.soundEffect().map((soundEvent) -> new EntityBoundSoundInstance(
                    soundEvent,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    entity,
                    player.level().random.nextLong())).orElse(null));
        }
    }

    public static void playSoundInstance(ResourceKey<Level> dimension, BlockPos blockPos, @Nullable AbstractSoundInstance soundInstance) {

        Optional.ofNullable(ACTIVE_BLOCK_SOUND_EFFECTS.get(dimension))
                .flatMap(soundInstances -> Optional.ofNullable(soundInstances.get(blockPos)))
                .ifPresent(oldSoundInstance -> Minecraft.getInstance().getSoundManager().stop(oldSoundInstance));

        clearInactiveSoundEffects();

        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            ACTIVE_BLOCK_SOUND_EFFECTS.computeIfAbsent(dimension, (x) -> new HashMap<>())
                    .put(blockPos, soundInstance);
        }
    }

    public static void playSoundInstance(UUID playerId, SlotReference reference, @Nullable AbstractSoundInstance soundInstance) {

        Optional.ofNullable(ACTIVE_PLAYER_SOUND_EFFECTS.get(playerId))
                .flatMap(soundInstances -> Optional.ofNullable(soundInstances.get(reference)))
                .ifPresent(oldSoundInstance -> Minecraft.getInstance().getSoundManager().stop(oldSoundInstance));

        clearInactiveSoundEffects();

        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            ACTIVE_PLAYER_SOUND_EFFECTS.computeIfAbsent(playerId, (x) -> new HashMap<>())
                    .put(reference, soundInstance);
        }
    }

    public static void clearInactiveSoundEffects() {

        Iterator<Map.Entry<ResourceKey<Level>, HashMap<BlockPos, AbstractSoundInstance>>> blockIterator = ACTIVE_BLOCK_SOUND_EFFECTS.entrySet().iterator();

        while (blockIterator.hasNext()) {
            Map<BlockPos, AbstractSoundInstance> soundInstances = blockIterator.next().getValue();
            Iterator<Map.Entry<BlockPos, AbstractSoundInstance>> levelIterator = soundInstances.entrySet().iterator();

            while (levelIterator.hasNext()) {
                AbstractSoundInstance soundInstance = levelIterator.next().getValue();

                if (!Minecraft.getInstance().getSoundManager().isActive(soundInstance)) {
                    levelIterator.remove();
                }
            }

            if (soundInstances.isEmpty()) {
                blockIterator.remove();
            }
        }

        Iterator<Map.Entry<UUID, HashMap<SlotReference, AbstractSoundInstance>>> playerIterator = ACTIVE_PLAYER_SOUND_EFFECTS.entrySet().iterator();

        while (playerIterator.hasNext()) {
            Map<SlotReference, AbstractSoundInstance> soundInstances = playerIterator.next().getValue();
            Iterator<Map.Entry<SlotReference, AbstractSoundInstance>> inventoryIterator = soundInstances.entrySet().iterator();

            while (inventoryIterator.hasNext()) {
                AbstractSoundInstance soundInstance = inventoryIterator.next().getValue();

                if (!Minecraft.getInstance().getSoundManager().isActive(soundInstance)) {
                    inventoryIterator.remove();
                }
            }

            if (soundInstances.isEmpty()) {
                playerIterator.remove();
            }
        }
    }
}
