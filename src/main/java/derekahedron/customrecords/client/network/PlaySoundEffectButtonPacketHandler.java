package derekahedron.customrecords.client.network;

import derekahedron.customrecords.client.util.ClientActiveSoundsManager;
import derekahedron.customrecords.network.PlaySoundEffectButtonInventoryPacket;
import derekahedron.customrecords.network.PlaySoundEffectButtonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PlaySoundEffectButtonPacketHandler {

    public static void handlePacket(PlaySoundEffectButtonPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (packet.isGlobal()) {
            ClientActiveSoundsManager.playSoundInstance(
                    packet.dimension(),
                    packet.blockPos(),
                    packet.soundEffect().map((soundEvent) ->
                            globalSoundInstance(soundEvent, player.level().random)).orElse(null));
        } else {
            ClientActiveSoundsManager.playSoundInstance(
                    player.level().dimension(),
                    packet.blockPos(),
                    packet.soundEffect().map((soundEvent) -> new SimpleSoundInstance(
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
            ClientActiveSoundsManager.playSoundInstance(
                    packet.playerId(),
                    packet.slotReference(),
                    packet.soundEffect().map((soundEvent) ->
                            globalSoundInstance(soundEvent, player.level().random)).orElse(null));
        } else {
            Entity entity = player.level().getPlayerByUUID(packet.playerId());
            if (entity == null) return;

            ClientActiveSoundsManager.playSoundInstance(packet.playerId(), packet.slotReference(), packet.soundEffect().map((soundEvent) -> new EntityBoundSoundInstance(
                    soundEvent,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    entity,
                    player.level().random.nextLong())).orElse(null));
        }
    }

    public static AbstractSoundInstance globalSoundInstance(SoundEvent soundEvent, RandomSource random) {
        return new SimpleSoundInstance(
                soundEvent.getLocation(),
                SoundSource.BLOCKS,
                1.0F,
                1.0F,
                random,
                false,
                0,
                SoundInstance.Attenuation.NONE,
                0.0D,
                0.0D,
                0.0D,
                true);
    }
}
