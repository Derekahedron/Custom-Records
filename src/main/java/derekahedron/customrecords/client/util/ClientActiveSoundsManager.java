package derekahedron.customrecords.client.util;

import com.google.common.collect.Maps;
import derekahedron.customrecords.util.Tuple;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class ClientActiveSoundsManager {

    public static final Map<Tuple<ResourceKey<Level>, BlockPos>, SoundInstance> ACTIVE_BLOCK_SOUND_EFFECTS = Maps.newHashMap();
    public static final Map<Tuple<UUID, SlotReference>, SoundInstance> ACTIVE_PLAYER_SOUND_EFFECTS = Maps.newHashMap();

    public static void playSoundInstance(
            ResourceKey<Level> dimension,
            BlockPos blockPos,
            @Nullable SoundInstance soundInstance) {
        var key = new Tuple<>(dimension, blockPos);
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        Optional.ofNullable(ACTIVE_BLOCK_SOUND_EFFECTS.get(key))
                .ifPresent(soundManager::stop);

        clearInactiveSoundEffects();

        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            ACTIVE_BLOCK_SOUND_EFFECTS.put(key, soundInstance);
        }
    }

    public static void playSoundInstance(
            UUID playerId,
            SlotReference reference,
            @Nullable SoundInstance soundInstance) {
        var key = new Tuple<>(playerId, reference);
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        Optional.ofNullable(ACTIVE_PLAYER_SOUND_EFFECTS.get(key))
                        .ifPresent(soundManager::stop);

        clearInactiveSoundEffects();

        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            ACTIVE_PLAYER_SOUND_EFFECTS.put(key, soundInstance);
        }
    }

    public static void clearInactiveSoundEffects() {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        ACTIVE_BLOCK_SOUND_EFFECTS.entrySet().removeIf(entry ->
                !soundManager.isActive(entry.getValue()));
        ACTIVE_PLAYER_SOUND_EFFECTS.entrySet().removeIf(entry ->
                !soundManager.isActive(entry.getValue()));
    }
}
