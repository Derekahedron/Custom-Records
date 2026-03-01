package derekahedron.customrecords.client.network;

import derekahedron.customrecords.item.CustomRecordItem;
import derekahedron.customrecords.network.PlaySophisticatedCustomRecordPacket;
import derekahedron.customrecords.sound.MusicTrack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.StorageSoundHandler;

public class PlaySophisticatedCustomRecordPacketHandler {

    public static void handlePacket(PlaySophisticatedCustomRecordPacket packet) {
        ClientLevel level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (level == null || player == null || !(packet.item() instanceof CustomRecordItem<?> item)) return;

        Holder.Reference<? extends MusicTrack> track = item.getMusicTrack(packet.trackId(), level);
        if (track == null) return;

        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(track.get().soundEvent());
        packet.source()
                .ifLeft(pos -> StorageSoundHandler.playStorageSound(soundEvent, packet.storageUuid(), pos))
                .ifRight(entityId -> StorageSoundHandler.playStorageSound(soundEvent, packet.storageUuid(), entityId));
    }
}
