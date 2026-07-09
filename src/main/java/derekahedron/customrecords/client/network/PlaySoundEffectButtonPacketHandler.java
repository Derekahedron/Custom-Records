package derekahedron.customrecords.client.network;

import com.google.common.collect.Maps;
import derekahedron.customrecords.network.PlaySoundEffectButtonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class PlaySoundEffectButtonPacketHandler {

    public static final Map<ResourceKey<Level>, Map<BlockPos, AbstractSoundInstance>> ACTIVE_BLOCK_SOUND_EFFECTS = Maps.newHashMap();

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

    public static void clearInactiveSoundEffects() {

        Iterator<Map.Entry<ResourceKey<Level>, Map<BlockPos, AbstractSoundInstance>>> blockIterator = ACTIVE_BLOCK_SOUND_EFFECTS.entrySet().iterator();

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
    }
}
