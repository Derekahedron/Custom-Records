package derekahedron.customrecords.client.network;

import derekahedron.customrecords.item.CustomRecordItem;
import derekahedron.customrecords.network.PlayCustomRecordPacket;
import derekahedron.customrecords.sound.MusicTrack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

public class PlayCustomRecordPacketHandler {

    public static void handlePacket(PlayCustomRecordPacket packet) {
        ClientLevel level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (level == null || player == null || !(packet.item() instanceof CustomRecordItem<?> item)) return;

        Holder.Reference<? extends MusicTrack> track = item.getMusicTrack(packet.trackId(), level);
        if (track == null) return;

        Minecraft.getInstance().levelRenderer.playStreamingMusic(
                SoundEvent.createVariableRangeEvent(track.get().soundEvent()),
                packet.blockPos(),
                null);

        String descriptionId = item.getDescriptionId(packet.trackId()) + ".desc";
        Minecraft.getInstance().gui.setNowPlaying(Component.translatable(descriptionId));
    }
}
