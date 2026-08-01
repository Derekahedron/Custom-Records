package derekahedron.customrecords.client.util;

import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ClientPressedSoundEffectButtonsManager {

    private static final HashSet<SlotReference> PRESSED_SLOTS = new HashSet<>();
    private static final WeakHashMap<Player, EnumSet<EquipmentSlot>> TRACKED_EQUIPMENT = new WeakHashMap<>();

    public static void setPressed(Player player, EquipmentSlot slot, boolean pressed) {
        if (TRACKED_EQUIPMENT.containsKey(player)) {
            EnumSet<EquipmentSlot> slots = TRACKED_EQUIPMENT.get(player);

            if (pressed) {
                slots.add(slot);
            } else {
                slots.remove(slot);

                if (slots.isEmpty()) {
                    TRACKED_EQUIPMENT.remove(player);
                }
            }
        } else {
            if (pressed) {
                TRACKED_EQUIPMENT.put(player, EnumSet.of(slot));
            }
        }
    }

    public static void setPressed(Player player, Collection<EquipmentSlot> slots) {
        if (slots.isEmpty()) {
            TRACKED_EQUIPMENT.remove(player);
        } else {
            TRACKED_EQUIPMENT.put(player, EnumSet.copyOf(slots));
        }
    }

    public static void setPressed(SlotReference slotReference, boolean pressed) {
        if (pressed) {
            PRESSED_SLOTS.add(slotReference);
        } else {
            PRESSED_SLOTS.remove(slotReference);
        }
    }

    public static boolean isPressed(Player player, SlotReference slotReference) {
        if (player != Minecraft.getInstance().player) return false;

        return PRESSED_SLOTS.contains(slotReference);
    }

    public static boolean isPressed(Player player, ItemStack stack) {
        if (player == Minecraft.getInstance().player) {
            for (SlotReference slotReference : PRESSED_SLOTS) {
                if (stack == slotReference.getStackForPlayer(player).orElse(null)) {
                    return true;
                }
            }

            return false;
        } else {
            if (!TRACKED_EQUIPMENT.containsKey(player)) return false;

            return CRUtil.getEquipmentSlot(player, stack)
                    .map(equipmentSlot -> TRACKED_EQUIPMENT.get(player).contains(equipmentSlot))
                    .orElse(false);
        }
    }

    public static void clear() {
        PRESSED_SLOTS.clear();
        TRACKED_EQUIPMENT.clear();
    }
}
