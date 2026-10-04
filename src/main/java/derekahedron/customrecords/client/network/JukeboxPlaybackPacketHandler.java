package derekahedron.customrecords.client.network;

import derekahedron.customrecords.client.util.ClientJukeboxPlaybackManager;
import derekahedron.customrecords.item.PortableJukeboxItemHandler;
import derekahedron.customrecords.network.*;
import derekahedron.customrecords.util.JukeboxPlayback;
import derekahedron.customrecords.util.Triple;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class JukeboxPlaybackPacketHandler {

    public static void handlePacket(PlayJukeboxTrackPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ClientJukeboxPlaybackManager.announceTrack(packet.playerId(), packet.slotReference(), packet.playback());
        ClientJukeboxPlaybackManager.startTrack(packet.playerId(), packet.slotReference(), packet.playback());
        ClientJukeboxPlaybackManager.addPlayback(packet.playerId(), packet.slotReference(), packet.playback());
        syncHandler(packet.playerId(), packet.slotReference());
    }

    public static void handlePacket(StopJukeboxTrackPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ClientJukeboxPlaybackManager.stopTrack(packet.playerId(), packet.slotReference());
        ClientJukeboxPlaybackManager.removePlayback(packet.playerId(), packet.slotReference());
        syncHandler(packet.playerId(), packet.slotReference());
    }

    public static void handlePacket(UpdateTrackedJukeboxPlaybacksPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        for (Triple<UUID, SlotReference, JukeboxPlayback> triple : packet.playbacks()) {
            UUID playerId = triple.a();
            SlotReference slotReference = triple.b();
            JukeboxPlayback playback = triple.c();

            ClientJukeboxPlaybackManager.startTrack(playerId, slotReference, playback);
            ClientJukeboxPlaybackManager.addPlayback(playerId, slotReference, playback);
            syncHandler(playerId, slotReference);
        }
    }

    private static void syncHandler(UUID playerId, SlotReference slotReference) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!playerId.equals(player.getUUID())) return;

        ClientJukeboxPlaybackManager.getPlayback(playerId, slotReference).ifPresentOrElse(
                playback -> PortableJukeboxItemHandler.getHandler(player, slotReference)
                        .filter(handler -> !playback.isSameTrack(handler))
                        .ifPresent(handler -> {
                            handler.selectedSlot = playback.slot;
                            handler.tickCount = 0;
                            handler.saveRecords();
                        }),
                () -> PortableJukeboxItemHandler.getHandler(player, slotReference).ifPresent(handler -> {
                    handler.tickCount = 0;
                    handler.saveTickCount();
                }));
    }

    public static void handlePacket(RemoveTrackedJukeboxPlaybacksPacket packet) {
        ClientJukeboxPlaybackManager.clearPlaybacks(packet.playerId());
    }
}
