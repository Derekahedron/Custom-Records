package derekahedron.customrecords.util;

import derekahedron.customrecords.item.SoundEffectButtonItem;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class PressedSoundEffectButtonsManager {

    public static final WeakHashMap<Player, HashMap<SlotReference, Integer>> PRESSED_BUTTONS = new WeakHashMap<>();

    public static void tick() {
        var playerIterator = PRESSED_BUTTONS.entrySet().iterator();

        while (playerIterator.hasNext()) {
            var playerEntry = playerIterator.next();
            Player player = playerEntry.getKey();
            var slots = playerEntry.getValue();

            var slotIterator = slots.entrySet().iterator();
            while (slotIterator.hasNext()) {
                var slotEntry = slotIterator.next();
                SlotReference slotReference = slotEntry.getKey();
                int newTicks = slotEntry.getValue() - 1;

                if (newTicks <= 0) {
                    slotIterator.remove();

                    ItemStack stack = slotReference.getStack(player).orElse(null);
                    if (stack != null && stack.getItem() instanceof SoundEffectButtonItem item) {
                        item.onInventoryUnpress(stack, player, slotReference);
                    }
                } else {
                    slotEntry.setValue(newTicks);
                }
            }

            if (slots.isEmpty()) {
                playerIterator.remove();
            }
        }
    }

    public static List<SlotReference> getPressedSlotReferences(Player player) {
        if (!PRESSED_BUTTONS.containsKey(player)) return List.of();

        return PRESSED_BUTTONS.get(player).entrySet().stream()
                .filter(entry -> entry.getValue() >= 0)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static int getPressedTicksRemaining(Player player, SlotReference slotReference) {
        if (!PRESSED_BUTTONS.containsKey(player)) return 0;
        return PRESSED_BUTTONS.get(player).getOrDefault(slotReference, 0);
    }

    public static boolean isPressed(Player player, SlotReference slotReference) {
        return getPressedTicksRemaining(player, slotReference) > 0;
    }

    public static void setPressedTicksRemaining(Player player, SlotReference slotReference, int ticksRemaining) {
        if (ticksRemaining > 0) {
            PRESSED_BUTTONS.computeIfAbsent(player, p -> new HashMap<>()).put(slotReference, ticksRemaining);
        } else {
            PRESSED_BUTTONS.get(player).remove(slotReference);
            if (PRESSED_BUTTONS.get(player).isEmpty()) {
                PRESSED_BUTTONS.remove(player);
            }
        }
    }
}
