package derekahedron.customrecords.client.network;

import derekahedron.customrecords.client.util.ClientPressedSoundEffectButtonsManager;
import derekahedron.customrecords.network.TrackPressedSoundEffectButtonPacket;
import derekahedron.customrecords.network.UpdatePressedSoundEffectButtonPacket;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.WeakHashMap;

public class UpdatePressedSoundEffectButtonPacketHandler {

    private static final WeakHashMap<Player, HashSet<SlotReference>> PRESSED_BUTTONS = new WeakHashMap<>();

    public static void handlePacket(UpdatePressedSoundEffectButtonPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Player player = level.getPlayerByUUID(packet.playerId());
        if (player == null) return;

        ClientPressedSoundEffectButtonsManager.setPressed(player, packet.slotReference(), packet.isPressed());
    }

    public static void handlePacket(TrackPressedSoundEffectButtonPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Player player = level.getPlayerByUUID(packet.playerId());
        if (player == null) return;

        if (PRESSED_BUTTONS.containsKey(player)) {
            PRESSED_BUTTONS.get(player).clear();
        }

        PRESSED_BUTTONS.put(player, new HashSet<>(packet.slotReferences()));
    }
}
