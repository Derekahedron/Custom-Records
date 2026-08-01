package derekahedron.customrecords.client.network;

import derekahedron.customrecords.client.util.ClientPressedSoundEffectButtonsManager;
import derekahedron.customrecords.network.UpdatePressedButtonPacket;
import derekahedron.customrecords.network.UpdateTrackedPressedButtonsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class UpdatePressedButtonsPacketHandler {

    public static void handlePacket(UpdateTrackedPressedButtonsPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Player player = level.getPlayerByUUID(packet.playerId());
        if (player == null) return;

        // This is only for other players. This should never get called,
        // but to avoid a memory leak, we return here anyway
        if (player == Minecraft.getInstance().player) return;

        packet.equipment()
                .ifLeft(tuple ->
                        ClientPressedSoundEffectButtonsManager.setPressed(player, tuple.a(), tuple.b()))
                .ifRight(slots ->
                        ClientPressedSoundEffectButtonsManager.setPressed(player, slots));
    }

    public static void handlePacket(UpdatePressedButtonPacket packet) {
        if (Minecraft.getInstance().player == null) return;

        ClientPressedSoundEffectButtonsManager.setPressed(packet.slotReference(), packet.isPressed());
    }
}
