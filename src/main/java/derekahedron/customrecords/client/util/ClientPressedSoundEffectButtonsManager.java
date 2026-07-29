package derekahedron.customrecords.client.util;

import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.WeakHashMap;

public class ClientPressedSoundEffectButtonsManager {

    private static final WeakHashMap<Player, HashSet<SlotReference>> PRESSED_BUTTONS = new WeakHashMap<>();

    public static boolean isPressed(Player player, SlotReference slotReference) {
        if (!PRESSED_BUTTONS.containsKey(player)) return false;

        return PRESSED_BUTTONS.get(player).contains(slotReference);
    }

    public static boolean isPressed(Player player, ItemStack stack) {
        if (!PRESSED_BUTTONS.containsKey(player)) return false;

        for (SlotReference slotReference : PRESSED_BUTTONS.get(player)) {
            if (stack == slotReference.getStackForPlayer(player).orElse(null)) {
                return true;
            }
        }

        return false;
    }

    public static void setPressed(Player player, SlotReference slotReference, boolean isPressed) {
        if (isPressed) {
            PRESSED_BUTTONS.computeIfAbsent(player, k -> new HashSet<>())
                    .add(slotReference);
        } else if (PRESSED_BUTTONS.containsKey(player)) {
            HashSet<SlotReference> playerButtons = PRESSED_BUTTONS.get(player);
            playerButtons.remove(slotReference);

            if (playerButtons.isEmpty()) {
                PRESSED_BUTTONS.remove(player);
            }
        }
    }
}
