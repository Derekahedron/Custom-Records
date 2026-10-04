package derekahedron.customrecords.client.util;

import com.google.common.collect.Maps;
import derekahedron.customrecords.client.sound.SeekingEntityBoundSoundInstance;
import derekahedron.customrecords.client.sound.SeekingSimpleSoundInstance;
import derekahedron.customrecords.sound.JukeboxTrack;
import derekahedron.customrecords.util.JukeboxPlayback;
import derekahedron.customrecords.util.JukeboxPlaybackManager;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.RandomSupport;

import javax.annotation.Nullable;
import java.util.*;

public class ClientJukeboxPlaybackManager {

    public static final HashMap<UUID, HashMap<SlotReference, JukeboxPlayback>> PLAYBACKS = Maps.newHashMap();
    @Nullable
    public static Long LAST_PAUSED_AT = null;

    public static void addPlayback(UUID playerId, SlotReference slotReference, JukeboxPlayback playback) {
        PLAYBACKS
                .computeIfAbsent(playerId, id -> Maps.newHashMap())
                .put(slotReference, playback);
    }

    public static void removePlayback(UUID playerId, SlotReference slotReference) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ClientActiveSoundsManager.playSoundInstance(playerId, slotReference, null);

        var playbacks = PLAYBACKS.get(playerId);
        if (playbacks == null) return;
        playbacks.remove(slotReference);
        if (playbacks.isEmpty()) PLAYBACKS.remove(playerId);
    }

    public static Optional<JukeboxPlayback> getPlayback(UUID playerId, SlotReference slotReference) {
        return Optional.ofNullable(PLAYBACKS.get(playerId))
                .flatMap(map -> Optional.ofNullable(map.get(slotReference)));
    }

    public static boolean isPlaying(ItemStack stack) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;

        var playbacks = PLAYBACKS.get(player.getUUID());
        if (playbacks == null) return false;

        return playbacks.entrySet().stream()
                .anyMatch(entry -> entry.getKey().getStackForPlayer(player)
                        .map(held -> held == stack)
                        .orElse(false));
    }

    public static void announceTrack(UUID playerId, SlotReference slotReference, JukeboxPlayback playback) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        playback.getTrack(player.level().registryAccess()).ifPresent(track -> {
            if (ClientJukeboxPlaybackManager.getPlayback(playerId, slotReference)
                    .map(oldPlayback -> !oldPlayback.isSameTrack(playback))
                    .orElse(true)
                    && canHearPlayback(playerId, playback)) {
                Minecraft.getInstance().gui.setNowPlaying(track.displayName());
            }
        });
    }

    public static void startTrack(UUID playerId, SlotReference slotReference, JukeboxPlayback playback) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        JukeboxTrack track = playback.getTrack(player.level().registryAccess()).orElse(null);
        if (track == null) {
            ClientActiveSoundsManager.playSoundInstance(playerId, slotReference, null);
            return;
        }

        int tickCount = playback.getTickCount(track.lengthInTicks());
        SoundInstance soundInstance;
        if (playback.isGlobal || playerId.equals(player.getUUID())) {
            soundInstance = ClientJukeboxPlaybackManager.makeGlobalSoundInstance(track.soundEvent(), tickCount);
        } else {
            Player otherPlayer = player.level().getPlayerByUUID(playerId);
            if (otherPlayer == null) return;
            soundInstance = ClientJukeboxPlaybackManager.makeEntityBoundSound(otherPlayer, track.soundEvent(), tickCount);
        }

        ClientActiveSoundsManager.playSoundInstance(playerId, slotReference, soundInstance);
    }

    public static void stopTrack(UUID playerId, SlotReference slotReference) {
        ClientActiveSoundsManager.playSoundInstance(playerId, slotReference, null);
    }

    public static void clearPlaybacks(UUID playerId) {
        var playbacks = PLAYBACKS.get(playerId);
        if (playbacks == null) return;

        playbacks.entrySet().stream()
                .filter(entry -> !entry.getValue().isGlobal)
                .map(Map.Entry::getKey)
                .toList()
                .forEach(slotReference -> removePlayback(playerId, slotReference));
    }

    public static void clear() {
        PLAYBACKS.clear();
        LAST_PAUSED_AT = null;
    }

    public static void delayPlaybacks(long millis) {
        PLAYBACKS.values().forEach(slots ->
                slots.values().forEach(playback -> playback.endTimestamp += millis));
    }

    public static void refreshSounds() {
        PLAYBACKS.forEach((playerId, playbacks) ->
                playbacks.forEach((slotReference, playback) ->
                        startTrack(playerId, slotReference, playback)));
    }

    public static SoundInstance makeGlobalSoundInstance(SoundEvent soundEvent, int tickCount) {
        return new SeekingSimpleSoundInstance(
                soundEvent.getLocation(),
                SoundSource.RECORDS,
                4.0F,
                1.0F,
                SoundInstance.createUnseededRandom(),
                false,
                0,
                SoundInstance.Attenuation.NONE,
                0.0D,
                0.0D,
                0.0D,
                true,
                tickCount / 20.0F);
    }

    public static SoundInstance makeEntityBoundSound(Player player, SoundEvent soundEvent, int tickCount) {
        return new SeekingEntityBoundSoundInstance(
                soundEvent,
                SoundSource.RECORDS,
                4.0F,
                1.0F,
                player,
                RandomSupport.generateUniqueSeed(),
                tickCount / 20.0F);
    }

    public static boolean canHearPlayback(UUID playerId, JukeboxPlayback playback) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;

        if (player.getUUID().equals(playerId)) return true;
        if (playback.isGlobal) return true;

        Player otherPlayer = player.level().getPlayerByUUID(playerId);
        if (otherPlayer == null) return false;

        return player.distanceTo(otherPlayer) < 64.0D;
    }

    public static void clearPaused() {
        if (LAST_PAUSED_AT != null) {
            long delay = Math.max(System.currentTimeMillis() - LAST_PAUSED_AT, 0);
            delayPlaybacks(delay);
            JukeboxPlaybackManager.delayPlaybacks(delay);
            LAST_PAUSED_AT = null;
        }
    }

    public static long getCurrentTimeMillisForPlaybacks() {
        return LAST_PAUSED_AT != null ? LAST_PAUSED_AT : System.currentTimeMillis();
    }
}
