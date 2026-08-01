package derekahedron.customrecords.util;

import derekahedron.customrecords.item.SoundEffectButtonItem;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.UpdateTrackedPressedButtonsPacket;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class PressedSoundEffectButtonsManager {

    public static final WeakHashMap<Player, HashMap<SlotReference, Integer>> PRESSED_SLOTS = new WeakHashMap<>();
    public static final WeakHashMap<Player, EnumSet<EquipmentSlot>> TRACKED_EQUIPMENT = new WeakHashMap<>();

    public static void tick(MinecraftServer server) {
        var playerIterator = PRESSED_SLOTS.entrySet().iterator();
        List<Tuple<Player, SlotReference>> toRemove = new ArrayList<>();

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
                    toRemove.add(new Tuple<>(player, slotReference));
                } else {
                    slotEntry.setValue(newTicks);
                }
            }

            if (slots.isEmpty()) {
                playerIterator.remove();
            }
        }

        for (Tuple<Player, SlotReference> tuple : toRemove) {
            ItemStack stack = tuple.b().getStackForPlayer(tuple.a()).orElse(null);
            if (stack != null && stack.getItem() instanceof SoundEffectButtonItem item) {
                item.onInventoryUnpress(stack, tuple.a(), tuple.b(), true);
            }
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            EnumSet<EquipmentSlot> oldEquipment = TRACKED_EQUIPMENT.get(player);

            EnumSet<EquipmentSlot> newEquipment = Optional.ofNullable(PRESSED_SLOTS.get(player))
                    .map(pressedSlots -> Arrays.stream(EquipmentSlot.values())
                            .filter(equipmentSlot ->
                                    pressedSlots.containsKey(CRUtil.getSlotReference(player, equipmentSlot)))
                            .toList())
                    .filter(list -> !list.isEmpty())
                    .map(EnumSet::copyOf)
                    .orElse(null);

            if (newEquipment == null) {
                if (oldEquipment != null) {
                    CRPacketHandler.INSTANCE.send(
                            PacketDistributor.TRACKING_ENTITY.with(() -> player),
                            new UpdateTrackedPressedButtonsPacket(
                                    player.getUUID(),
                                    Collections.emptySet()));
                    TRACKED_EQUIPMENT.remove(player);
                }
            } else if (oldEquipment == null || !oldEquipment.equals(newEquipment)) {
                CRPacketHandler.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY.with(() -> player),
                        new UpdateTrackedPressedButtonsPacket(
                                player.getUUID(),
                                newEquipment));
                TRACKED_EQUIPMENT.put(player, newEquipment);
            }
        }
    }

    public static void clear() {
        PRESSED_SLOTS.clear();
        TRACKED_EQUIPMENT.clear();
    }

    public static Collection<EquipmentSlot> getPressedEquipmentSlots(Player player) {
        return Optional.<Collection<EquipmentSlot>>ofNullable(TRACKED_EQUIPMENT.get(player))
                .orElse(Collections.emptySet());
    }

    public static int getPressedTicksRemaining(Player player, SlotReference slotReference) {
        if (!PRESSED_SLOTS.containsKey(player)) return 0;
        return PRESSED_SLOTS.get(player).getOrDefault(slotReference, 0);
    }

    public static boolean isPressed(Player player, SlotReference slotReference) {
        return getPressedTicksRemaining(player, slotReference) > 0;
    }

    public static void setPressedTicksRemaining(ServerPlayer player, SlotReference slotReference, int ticksRemaining) {
        HashMap<SlotReference, Integer> pressedSlots = PRESSED_SLOTS.get(player);

        if (ticksRemaining > 0) {

            if (pressedSlots == null) {
                pressedSlots = new HashMap<>();
                PRESSED_SLOTS.put(player, pressedSlots);
            }

            pressedSlots.put(slotReference, ticksRemaining);
        } else {

            if (pressedSlots != null) {
                pressedSlots.remove(slotReference);

                if (pressedSlots.isEmpty()) {
                    PRESSED_SLOTS.remove(player);
                }
            }
        }
    }
}
