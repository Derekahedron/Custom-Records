package derekahedron.customrecords.event;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.RemoveTrackedJukeboxPlaybacksPacket;
import derekahedron.customrecords.network.UpdateTrackedJukeboxPlaybacksPacket;
import derekahedron.customrecords.util.JukeboxPlaybackManager;
import derekahedron.customrecords.util.Triple;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JukeboxPlaybackEventHandler {

    @SubscribeEvent
    public static void clearPlaying(ServerAboutToStartEvent event) {
        JukeboxPlaybackManager.clear();
    }

    @SubscribeEvent
    public static void tickPlaying(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side != LogicalSide.SERVER) return;
        JukeboxPlaybackManager.tick(event.getServer());
    }

    @SubscribeEvent
    public static void sendGlobalPlaying(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var playbacks = JukeboxPlaybackManager.getGlobalJukeboxPlaybacks();
        if (playbacks.isEmpty()) return;

        CRPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new UpdateTrackedJukeboxPlaybacksPacket(playbacks));
    }

    @SubscribeEvent
    public static void trackPlaying(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof ServerPlayer tracked)
                || !(event.getEntity() instanceof ServerPlayer player)) return;

        var tracks = JukeboxPlaybackManager.getTrackedJukeboxTracks(tracked);
        if (tracks.isEmpty()) return;

        CRPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new UpdateTrackedJukeboxPlaybacksPacket(
                        tracks.stream().map(tuple ->
                                new Triple<>(tracked.getUUID(), tuple.a(), tuple.b()))
                                .toList()
                ));
    }

    @SubscribeEvent
    public static void unTrackPlaying(PlayerEvent.StopTracking event) {
        if (!(event.getTarget() instanceof ServerPlayer tracked)
                || !(event.getEntity() instanceof ServerPlayer player)) return;

        CRPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new RemoveTrackedJukeboxPlaybacksPacket(tracked.getUUID()));
    }
}
