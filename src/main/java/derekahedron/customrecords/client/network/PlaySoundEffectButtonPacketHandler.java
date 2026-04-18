package derekahedron.customrecords.client.network;

import com.google.common.collect.Maps;
import derekahedron.customrecords.network.PlayGlobalSoundEffectButtonPacket;
import derekahedron.customrecords.network.PlaySoundEffectButtonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class PlaySoundEffectButtonPacketHandler {
    public static final Map<ResourceKey<Level>, Map<BlockPos, SoundInstance>> activeSoundEffects = Maps.newHashMap();

    public static void handlePacket(PlaySoundEffectButtonPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

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

    public static void handlePacket(PlayGlobalSoundEffectButtonPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

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
    }

    public static void playSoundInstance(ResourceKey<Level> dimension, BlockPos blockPos, @Nullable SoundInstance soundInstance) {

        Optional.ofNullable(activeSoundEffects.get(dimension))
                .flatMap(soundInstances -> Optional.ofNullable(soundInstances.get(blockPos)))
                .ifPresent(oldSoundInstance -> Minecraft.getInstance().getSoundManager().stop(oldSoundInstance));

        clearInactiveSoundEffects();

        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            activeSoundEffects.computeIfAbsent(dimension, (x) -> new HashMap<>())
                    .put(blockPos, soundInstance);
        }
    }

    public static void clearInactiveSoundEffects() {
        Iterator<Map.Entry<ResourceKey<Level>, Map<BlockPos, SoundInstance>>> iterator = activeSoundEffects.entrySet().iterator();

        while (iterator.hasNext()) {
            Map<BlockPos, SoundInstance> soundInstances = iterator.next().getValue();
            Iterator<Map.Entry<BlockPos, SoundInstance>> levelIterator = soundInstances.entrySet().iterator();

            while (levelIterator.hasNext()) {
                SoundInstance soundInstance = levelIterator.next().getValue();
                if (!Minecraft.getInstance().getSoundManager().isActive(soundInstance)) {
                    levelIterator.remove();
                }
            }

            if (soundInstances.isEmpty()) {
                iterator.remove();
            }
        }
    }
}
