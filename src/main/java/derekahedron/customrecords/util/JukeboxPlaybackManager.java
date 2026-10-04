package derekahedron.customrecords.util;

import com.google.common.collect.Maps;
import derekahedron.customrecords.client.util.ClientJukeboxPlaybackManager;
import derekahedron.customrecords.item.PortableJukeboxItemHandler;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.PlayJukeboxTrackPacket;
import derekahedron.customrecords.network.StopJukeboxTrackPacket;
import derekahedron.customrecords.network.UpdateTrackedJukeboxPlaybacksPacket;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class JukeboxPlaybackManager {

    public static final HashMap<UUID, HashMap<SlotReference, JukeboxPlayback>> PLAYBACKS = Maps.newHashMap();

    public static void tick(MinecraftServer server) {

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> ClientJukeboxPlaybackManager::clearPaused);

        var playerIterator = PLAYBACKS.entrySet().iterator();

        while (playerIterator.hasNext()) {
            var playerEntry = playerIterator.next();
            UUID playerId = playerEntry.getKey();
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            var slots = playerEntry.getValue();

            // Case for if the player is gone somehow.
            if (player == null) {
                slots.forEach((slotReference, playback) ->
                        CRPacketHandler.INSTANCE.send(
                                PacketDistributor.ALL.noArg(),
                                new StopJukeboxTrackPacket(
                                        playerId,
                                        slotReference)));
                playerIterator.remove();
                continue;
            }

            var slotIterator = slots.entrySet().iterator();
            while (slotIterator.hasNext()) {
                var slotEntry = slotIterator.next();
                SlotReference slotReference = slotEntry.getKey();
                JukeboxPlayback playback = slotEntry.getValue();
                boolean playbackEnded = playback.hasEnded();

                PortableJukeboxItemHandler handler = PortableJukeboxItemHandler.getHandler(player, slotReference)
                        .filter(playback::isSameTrack)
                        .orElse(null);

                if (handler == null) {
                    stopTrack(player, slotReference, playback.isGlobal, true);
                    slotIterator.remove();
                } else if (playbackEnded && !handler.isAutoplay) {
                    handler.tickCount = 0;
                    handler.saveTickCount();
                    stopTrack(player, slotReference, playback.isGlobal, true);
                    slotIterator.remove();
                } else if (playbackEnded) {
                    handler.selectedSlot = handler.getSlotAtOffset(
                            handler.selectedSlot,
                            1,
                            player.level().registryAccess());
                    handler.saveSelectedSlot();

                    if (handler.selectedSlot == -1) {
                        handler.tickCount = 0;
                        handler.saveTickCount();
                        stopTrack(player, slotReference, playback.isGlobal, true);
                        slotIterator.remove();
                    } else {
                        JukeboxPlayback newPlayback = handler.createNewPlayback(player.level().registryAccess(), 0)
                                .orElse(null);

                        if (newPlayback != null) {
                            // Stop playing previous global playback first
                            if (playback.isGlobal && !newPlayback.isGlobal) {
                                stopTrack(player, slotReference, true, false);
                            }
                            startTrack(player, slotReference, newPlayback, true);
                            slotEntry.setValue(newPlayback);
                        }
                    }
                }
            }

            if (slots.isEmpty()) {
                playerIterator.remove();
            }
        }
    }

    public static Optional<JukeboxPlayback> getPlayback(ServerPlayer player, SlotReference slotReference) {
        return Optional.ofNullable(PLAYBACKS.get(player.getUUID()))
                .flatMap(map -> Optional.ofNullable(map.get(slotReference)));
    }

    public static void addPlayback(ServerPlayer player, SlotReference slotReference, JukeboxPlayback playback) {
        PLAYBACKS
                .computeIfAbsent(player.getUUID(), key -> new HashMap<>())
                .put(slotReference, playback);
    }

    public static void removePlayback(ServerPlayer player, SlotReference slotReference) {
        UUID playerId = player.getUUID();
        var slots = PLAYBACKS.get(playerId);
        if (slots == null) return;

        slots.remove(slotReference);
        if (slots.isEmpty()) {
            PLAYBACKS.remove(playerId);
        }
    }

    public static void startTrack(
            ServerPlayer player,
            SlotReference slotReference,
            JukeboxPlayback playback,
            boolean updatePlayer) {
        if (getPlayback(player, slotReference)
                .map(oldPlayback -> !oldPlayback.isSameTrack(playback))
                .orElse(true)) {
            player.awardStat(Stats.PLAY_RECORD);
            player.gameEvent(GameEvent.JUKEBOX_PLAY);
        }

        if (playback.isGlobal) {
            CRPacketHandler.INSTANCE.send(
                    updatePlayer
                            ? PacketDistributor.ALL.noArg()
                            : CRPacketHandler.ALL_BUT_PLAYER.with(() -> player),
                    new PlayJukeboxTrackPacket(
                            player.getUUID(),
                            slotReference,
                            playback));
        } else {
            CRPacketHandler.INSTANCE.send(
                    updatePlayer
                            ? PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player)
                            : PacketDistributor.TRACKING_ENTITY.with(() -> player),
                    new PlayJukeboxTrackPacket(
                            player.getUUID(),
                            slotReference,
                            playback));
        }
    }

    public static void stopTrack(
            ServerPlayer player,
            SlotReference slotReference,
            boolean isGlobal,
            boolean updatePlayer) {
        player.gameEvent(GameEvent.JUKEBOX_STOP_PLAY);

        if (isGlobal) {
            CRPacketHandler.INSTANCE.send(
                    updatePlayer
                            ? PacketDistributor.ALL.noArg()
                            : CRPacketHandler.ALL_BUT_PLAYER.with(() -> player),
                    new StopJukeboxTrackPacket(
                            player.getUUID(),
                            slotReference));
        } else {
            CRPacketHandler.INSTANCE.send(
                    updatePlayer
                            ? PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player)
                            : PacketDistributor.TRACKING_ENTITY.with(() -> player),
                    new StopJukeboxTrackPacket(
                            player.getUUID(),
                            slotReference));
        }
    }

    public static void clear() {
        PLAYBACKS.clear();
    }

    public static void delayPlaybacks(long millis) {
        PLAYBACKS.values().forEach(slots ->
                slots.values().forEach(playback -> playback.endTimestamp += millis));
    }

    public static Collection<Tuple<SlotReference, JukeboxPlayback>> getTrackedJukeboxTracks(ServerPlayer player) {
        UUID playerId = player.getUUID();
        var slots = PLAYBACKS.get(playerId);
        if (slots == null) return List.of();

        List<Tuple<SlotReference, JukeboxPlayback>> tracks = new ArrayList<>(slots.size());

        slots.forEach((slotReference, playback) -> {
            if (playback.isGlobal) return;

            PortableJukeboxItemHandler.getHandler(player, slotReference).ifPresent(handler -> {
                ItemStack record = handler.getSelectedStack();
                if (record.isEmpty()) return;

                tracks.add(new Tuple<>(
                        slotReference,
                        playback));
            });
        });

        return tracks;
    }

    public static Collection<Triple<UUID, SlotReference, JukeboxPlayback>> getGlobalJukeboxPlaybacks() {
        return PLAYBACKS.entrySet().stream()
                .flatMap(playerEntry -> playerEntry.getValue().entrySet().stream()
                        .filter(slotEntry -> slotEntry.getValue().isGlobal)
                        .map(slotEntry -> new Triple<>(
                                playerEntry.getKey(),
                                slotEntry.getKey(),
                                slotEntry.getValue())))
                .limit(UpdateTrackedJukeboxPlaybacksPacket.UPDATE_LIMIT)
                .toList();
    }
}
